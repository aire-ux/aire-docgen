import {customElement, html, LitElement, PropertyValues, query} from "lit-element";

import styles from 'styles/standard-application-layout.css'

export type SlotChangedEvent = {}

@customElement('standard-application-layout')
export default class StandardApplicationLayout extends LitElement {


  /**
   *
   */
  @query('slot[name="header"]')
  header: HTMLSlotElement;

  @query('slot[name="navigation"]')
  navigation: HTMLSlotElement;


  /**
   * the actual navigation element
   */
  navigationElement: HTMLElement;

  /**
   * update the navigation element from a slot changed event
   * @param event the slot changed event (unused by this by default)
   */
  private updateNavigationElement = (event: SlotChangedEvent) =>
      this.navigationElement = this.navigation.assignedElements()[0] as HTMLElement;


  static styles = [
    styles,
  ]

  render() {
    return html`
      <main>
        <slot name="header"></slot>
        <slot name="navigation"></slot>
        <slot name="content"></slot>
        <slot name="footer"></slot>
      </main>
    `;
  }


  protected firstUpdated(changed: PropertyValues) {
    super.firstUpdated(changed);
    this.navigation.addEventListener('slotchange', this.updateNavigationElement);
  }


  private openNav(): void {
    this.navigationElement.style.width = '250px';

    // let child = this.header.children[0] as HTMLElement;
    // console.log(child);
    // child.style.width = '250px';
  }

}