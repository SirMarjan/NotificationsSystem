import {css, html, LitElement} from "lit";
import "./templates-list.js"
import "./template-form.js"
import resetCss from "./common-css.js"

class AppElement extends LitElement {

    static properties = {
        _selectedTemplateId: {state: true}
    }

    constructor() {
        super();
        this._selectedTemplateId = null;
    }

    static styles = [
        ...resetCss,
        css`
            :host {
                display: flex;
                flex-direction: row;
                gap: 8px;
            }
        `]

    render() {
        return html`
            <div class="column">
                <templates-list .selectedTemplateId=${this._selectedTemplateId}
                                @template-selected=${this._handleSelectedTemplateChange}></templates-list>
            </div>
            <div class="column">
                <template-form @template-saved=${this._handleSelectedTemplateChange}
                               .templateId="${this._selectedTemplateId}"></template-form>
            </div>
        `;
    }

    _handleSelectedTemplateChange(event) {
        console.log(event)
        this._selectedTemplateId = event.detail.templateId;
    }
}

window.customElements.define('app-element', AppElement)