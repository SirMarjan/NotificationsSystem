import {css, html, LitElement} from "lit";
import resetCss from "./common-css.js";
import {repeat} from "lit/directives/repeat.js";
import {TemplateService} from "./services/TemplateService.js";

class TemplatesList extends LitElement {

    static properties = {
        selectedTemplateId: {type: String},
        _templates: {state: true}
    }

    constructor() {
        super();
        this.selectedTemplateId = null
        this._templates = []
        this._templateService = new TemplateService()
    }

    updated(changedProperties) {
        if (changedProperties.has('selectedTemplateId')) {
            console.log(changedProperties)
            this._refreshTemplates(changedProperties['selectedTemplateId'])
        }
    }

    static styles = [
        ...resetCss,
        css`
            :host {
                padding: 24px 0;
                background-color: lightgray;
                display: flex;
                flex-direction: column;
                gap: 12px;
                height: 100%
            }

            button.add {
                margin: 0 12px;
                width: fit-content;
            }

            div.list {
                display: flex;
                flex-direction: column;
            }

            div.item {
                padding: 8px 12px;
            }

            div.selected {
                background-color: gray;
            }
        `]

    render() {
        return html`
            <button class="add" type="button" @click=${(e) => this._selectTemplate(e, null)}>
                Dodaj nowy
            </button>
            <div class="list">
                ${repeat(
                        this._templates,
                        (template) => template.id,
                        (template) => this._renderListElement(template)
                )}
            </div>
        `;
    }

    _renderListElement(template) {
        return html`
            <div @click="${(e) => this._selectTemplate(e, template.id)}"
                 class="${'item' + (this.selectedTemplateId === template.id ? ' selected' : '')}">
                ${template.title}
            </div>
        `
    }

    _selectTemplate(event, templateId) {
        event.preventDefault()
        this.selectedTemplateId = templateId

        this.dispatchEvent(
            new CustomEvent('template-selected', {
                detail: {templateId: templateId},
                bubbles: true,
                composed: true
            })
        )
    }

    _refreshTemplates() {
        this._templateService.getTemplatesList()
            .then(templates => {
                this._templates = templates;
            });
    }

}

window.customElements.define('templates-list', TemplatesList)