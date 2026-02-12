import {css, html, LitElement} from "lit";
import resetCss from "./common-css.js";
import {repeat} from 'lit/directives/repeat.js';
import {TemplateService} from "./services/TemplateService.js";

class TemplateForm extends LitElement {

    static properties = {
        templateId: {type: String},
        _receiversEmailsIds: {state: true},
        _rules: {state: true},
        _formData: {state: true}
    }

    constructor() {
        super();
        this.templateId = null
        this._receiversEmailsIds = []
        this._rules = {}
        this._templateService = new TemplateService()
        this._formData = {}

        this._ruleOperators = [
            {code: "ITEM_IS", text: "Item is", type: "ITEM"},
            {code: "ITEM_IS_NOT", text: "Item is not", type: "ITEM"},
            {code: "PRICE_EQUALS", text: "Price is equal to", type: "PRICE"},
            {code: "PRICE_GREATER_OR_EQUALS", text: "Price is greater than or equal to", type: "PRICE"},
            {code: "PRICE_GREATER", text: "Price is greater than", type: "PRICE"},
            {code: "PRICE_LESS_OR_EQUALS", text: "Price is less than", type: "PRICE"},
            {code: "PRICE_LESS", text: "Price is less than or equal to", type: "PRICE"},
        ]

        this._items = [
            {code: "GOLD", text: "gold"},
            {code: "SILVER", text: "silver"},
            {code: "PLATINUM", text: "platinum"}
        ]
    }

    willUpdate(_changedProperties) {
        super.willUpdate(_changedProperties);
        if (_changedProperties.has('templateId')) {
            if (this.templateId) {
                console.log("Update", this.templateId)
                this._resetForm(true)
                this._loadTemplate(this.templateId)
            } else {
                this._resetForm()
                console.log("Empty")
            }
        }
    }

    static styles = [
        ...resetCss,
        css`
            :host {
                display: flex;
                flex-direction: column;
            }

            .control-group {
                margin: 8px 0;
                display: flex;
                gap: 8px;
                flex-direction: column;
            }

            .control-subgroup {
                display: flex;
                gap: 4px;
                flex-direction: row;
            }

            .control-group label {
                display: block;
            }

            :invalid {
                border-color: red;
            }
        `]

    render() {
        return html`
            <form @submit=${this._handleSubmit}>
                <div class="control-group">
                    <label for="tile-input">Tytuł</label>
                    <input id="title-input" type="text" name="title" required .value=${this._formData.title ?? null}>
                </div>
                <fieldset class="control-group">
                    <legend>Odbiorcy</legend>
                    ${repeat(
                            this._receiversEmailsIds,
                            (id) => id,
                            (id, ix) => this._renderReceiver(id, ix)
                    )}
                    <button @click=${this._addReceiver} type="button">Dodaj</button>
                </fieldset>
                <div class="control-group">
                    <label for="content-input">Treść</label>
                    <textarea id="content-input" name="content" required
                              .value=${this._formData.content ?? null}></textarea>
                </div>
                <fieldset class="control-group">
                    <legend>Reguły wysyłki</legend>
                    ${repeat(
                            Object.keys(this._rules),
                            (ruleId) => ruleId,
                            (id, ix) => this._renderRule(id, ix)
                    )}
                    <button @click=${this._addRule} type="button">Dodaj</button>
                </fieldset>
                <button type="submit">Zapisz</button>
            </form>
        `;
    }

    _renderReceiver(receiverEmailId, index) {
        const receiverEmail = this._formData.receiversEmails?.[index] ?? null
        return html`
            <div class="control-subgroup">
                <label for="receiver-email-${receiverEmailId}" class="visually-hidden">Email odbiorcy
                    ${index + 1}</label>
                <input type="email" id="receiver-email-${receiverEmailId}" name="receiverEmail" required
                       .value=${receiverEmail}>
                <button @click=${(event) => this._removeReceiver(event, receiverEmailId)} type="button">Usuń</button>
            </div>
        `
    }

    _renderRule(ruleId, index) {
        const operator = this._formData.rules?.[index]?.operator ?? null;
        const operand = this._formData.rules?.[index]?.operand ?? null;
        return html`
            <div>

                <label for="rule-operator-${ruleId}" class="visually-hidden">Regóła wysyłki ${index + 1}</label>
                <select name="ruleOperator" id="rule-operator-${ruleId}"
                        @change=${(e) => this._changeRuleType(e.target.value, ruleId)}>
                    ${this._ruleOperators.map(ruleOperator => html`
                        <option ?selected=${operator === ruleOperator.code} value=${ruleOperator.code}>
                            ${ruleOperator.text}
                        </option>`)}
                </select>

                ${
                        this._rules[ruleId] === 'PRICE'
                                ? html`
                                    <label for="rule-operand-${ruleId}" class="visually-hidden">Cena dla reguły
                                        ${index}</label>
                                    <input type="number" step="any" id="rule-operand-${ruleId}" name="ruleOperand" required
                                           min="0" .value=${operand}>
                                `
                                : html`
                                    <label for="rule-operand-${ruleId}" class="visually-hidden">Typ kruszca ${index}</label>
                                    <select name="ruleOperand" id="rule-operand-${ruleId}" required .value=${operand}>
                                        ${this._items.map(item => html`
                                            <option ?selected=${operand === item.code} value=${item.code}>${item.text}</option>
                                        `)}
                                    </select>
                                `
                }
            </div>
        `
    }

    _addRule(event) {
        event?.preventDefault();
        const ruleId = String(Math.random())
        this._rules = {
            ...this._rules,
            [ruleId]: this._ruleOperators[0].type
        }
        return ruleId
    }

    _changeRuleType(value, ruleId) {
        const selectedType = this._ruleOperators.find(operator => operator.code === value).type
        this._rules = {
            ...this._rules,
            [ruleId]: selectedType
        }
    }

    _addReceiver(event) {
        event?.preventDefault();
        this._receiversEmailsIds = [...this._receiversEmailsIds, String(Math.random())]
    }

    _removeReceiver(event, receiverEmailId) {
        event.preventDefault();
        this._receiversEmailsIds = this._receiversEmailsIds.filter(id => id !== receiverEmailId)
    }

    _handleSubmit(event) {
        event.preventDefault();
        const formData = new FormData(event.target);
        const template = {
            title: formData.get("title"),
            receiversEmails: formData.getAll("receiverEmail"),
            content: formData.get("content"),
            rules: []
        }

        const ruleOperators = formData.getAll('ruleOperator');
        const ruleOperands = formData.getAll('ruleOperand');

        ruleOperators.forEach((operator, ix) => {
            template.rules.push({
                operator: operator,
                operand: ruleOperands[ix]
            })
        })

        if (this.templateId) {
            this._templateService.updateTemplate(template, this.templateId)
                .then(() => {
                    this.dispatchEvent(new CustomEvent('template-saved', {
                        detail: {templateId: this.templateId},
                        bubbles: true,
                        composed: true
                    }))
                })
                .catch(err => console.log(err))
        } else {
            this._templateService.addNewTemplate(template)
                .then(jsonResponse => {
                    this.dispatchEvent(new CustomEvent('template-saved', {
                        detail: {templateId: jsonResponse.id},
                        bubbles: true,
                        composed: true
                    }))
                })
                .catch(err => console.log(err))
        }
    }

    _loadTemplate(templateId) {
        this._templateService.getTemplateDetails(templateId)
            .then((template) => {
                this._formData = template
                this._formData.receiversEmails?.forEach(() => this._addReceiver())
                this._formData.rules?.forEach((rule) => {
                    const ruleId = this._addRule()
                    this._changeRuleType(rule.operator, ruleId)
                })
            })
    }

    _resetForm(soft) {
        const form = this.shadowRoot.querySelector('form');
        if (form) {
            this._receiversEmailsIds = []
            this._rules = []
            this._formData = {}
            if (!soft) {
                form.reset()
            }

        }
    }
}

window.customElements.define('template-form', TemplateForm)
