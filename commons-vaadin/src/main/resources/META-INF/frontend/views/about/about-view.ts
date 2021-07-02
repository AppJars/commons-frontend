import { customElement, html, LitElement, unsafeCSS } from 'lit-element';
import { CSSModule } from '@vaadin/flow-frontend/css-utils';
import styles from './about-view.css';

@customElement('about-view')
export class AboutView extends LitElement {
  static get styles() {
    return [CSSModule('lumo-typography'), unsafeCSS(styles)];
  }

  render() {
    return html`
      <div class="about-section">
        <h1>About Inventory Management App</h1>
        <p>This project is part of AppJars project.</p>
        <p>Visit our <a href="https://github.com/AppJars">GitHub Organization</a> for more information.</p>
      </div>
    `;
  }
}
