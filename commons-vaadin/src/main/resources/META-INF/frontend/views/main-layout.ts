import { CSSModule } from '@vaadin/flow-frontend/css-utils';
// @ts-ignore
import { css, customElement, html, LitElement, property, query } from 'lit-element';
//import { router } from '../index';
import "@flowingcode/fc-applayout";
import {FcAppLayoutElement} from "@flowingcode/fc-applayout/src/fc-applayout";
import "@flowingcode/fc-menuitem";


@customElement('main-layout')
export class MainLayout extends LitElement {
//  @property({ type: Object }) location = router.location;

  @query("#mainLayout")
  fcAppLayout!: FcAppLayoutElement;

  constructor() {
    super();
    this.addEventListener("menuitem-clicked-event", (e:Event) => {
      let fcmi: any = e.composedPath()[0];
      if (!fcmi.isSubmenu) {
        this.fcAppLayout.drawer.close();
      }
    });
  }

  static get styles() {
    return [
      CSSModule('lumo-typography'),
      CSSModule('lumo-color'),
      css`
        :host {
          display: block;
          height: 100%;
        }

        header {
          align-items: center;
          box-shadow: var(--lumo-box-shadow-s);
          display: flex;
          height: var(--lumo-size-xl);
          width: 100%;
        }

        header h1 {
          font-size: var(--lumo-font-size-l);
          margin: 0;
        }

        header img {
          border-radius: 50%;
          height: var(--lumo-size-s);
          margin-left: auto;
          margin-right: var(--lumo-space-m);
          overflow: hidden;
          background-color: var(--lumo-contrast);
        }

        #logo {
          align-items: center;
          box-sizing: border-box;
          display: flex;
          padding: var(--lumo-space-s) var(--lumo-space-m);
        }

        #logo img {
          height: calc(var(--lumo-size-l) * 1.5);
        }

        #logo span {
          font-size: var(--lumo-font-size-xl);
          font-weight: 600;
          margin: 0 var(--lumo-space-s);
        }

        .other {
          background-color: white;
        }
        .current {
          background-color: green;
        }
		

      `,
    ];
  }

  render() {
    return html`
    <fc-applayout title="Inventory Management" id="mainLayout">
      <div slot="profile" style="text-align: center;">
        <img src="./images/user.svg" alt="avatar" style="width: 80px; margin-top: 20px;">
        <h4 slot="profile">User</h4>
      </div>
      <img slot="title" class="applogo" src="./icons/icon.png" alt="applogo" style="width:50px">
      <div slot="title" main-title="">Inventory Management</div>
      <paper-icon-button slot="toolbar" icon="settings" title="Settings" role="button" tabindex="0" aria-disabled="false"></paper-icon-button>
      <div slot="menu" tabindex="0" aria-selected="false">
		    <fc-menuitem label="Sub Menu">
          <fc-menuitem label="Hello world" slot="menu-item" href="/hello"></fc-menuitem>
		      <fc-menuitem label="External link" slot="menu-item" href="https://www.google.com"></fc-menuitem>
		    </fc-menuitem>
		    <fc-menuitem label="Login" href="/" .class=${this.currentLocationClass("")}></fc-menuitem>
		    <fc-menuitem label="Logout" href="/logout" .class=${this.currentLocationClass("logout")}></fc-menuitem>
		    <fc-menuitem label="About" href="/about" .class=${this.currentLocationClass("about")}></fc-menuitem>
      </div>
      <div slot="content">
        <slot></slot>
      </div>
    </fc-applayout>		

     
    `;
  }

  connectedCallback() {
    super.connectedCallback();
  }

  disconnectedCallback() {
    super.disconnectedCallback();
  }

  private currentLocationClass(route: string): string {
    return /*(router.urlForPath(route) === this.location.getUrl()?"current":"other")*/ "";
  }
}
