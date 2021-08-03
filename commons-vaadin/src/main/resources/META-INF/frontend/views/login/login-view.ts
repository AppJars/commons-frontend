import '@vaadin/vaadin-button';
import '@vaadin/vaadin-text-field';
import { css, customElement, html, LitElement } from 'lit-element';

@customElement('login-view')
export class LoginView extends LitElement {
  name: string = '';

  static get styles() {
    return css`
      :host {
        display: flex;
      }
      iframe {
        width: 100%;
        height: calc(100vh + 64px);
      }
    `;
  }
  render() {
    return html`
      <iframe src="/inner-welcome" frameBorder="0"></iframe>
    `;
  }
  nameChanged(e: CustomEvent) {
    this.name = e.detail.value;
  }

}
