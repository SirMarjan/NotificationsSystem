import normalize from "modern-normalize/modern-normalize.css?inline"
import {css, unsafeCSS} from "lit";

export default [
    unsafeCSS(normalize),
    css`
        .visually-hidden {
            position: absolute;
            width: 1px;
            height: 1px;
            padding: 0;
            margin: -1px;
            overflow: hidden;
            clip: rect(0, 0, 0, 0);
            white-space: nowrap;
            border: 0;
        }
    `]
