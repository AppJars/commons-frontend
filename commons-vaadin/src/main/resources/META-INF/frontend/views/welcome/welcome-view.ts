import { css, customElement, html, LitElement } from 'lit-element';

@customElement('welcome-view')
export class WelcomeView extends LitElement {
  name: string = '';

  static get styles() {
    return css`
      :host {
        display: block;
        padding: 1em;
      }
    `;
  }
  render() {
    return html`
      <p>Welcome to Application Template</p>
    `;
  }
}
