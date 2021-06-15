import {customElement, html, LitElement, property, PropertyValues, query} from "lit-element";

import styles from 'styles/standard-application-layout.css'

export type SlotChangedEvent = {}

export enum NavigationState {
  Open = "Open",
  Closed = "Closed"
}

@customElement('standard-application-layout')
export default class StandardApplicationLayout extends LitElement {

  public static NAVIGATION_STATE_CHANGED_EVENT = 'navigation-state-changed';


  /**
   *
   */
  @query('slot[name="header"]')
  public header: HTMLSlotElement;

  @query('slot[name="navigation"]')
  public navigation: HTMLSlotElement;

  /**
   * the width of the navigation when it's in its open state
   */

  @property({type: String})
  public width: string;

  /**
   * the navigation state (open or closed)
   */
  @property({type: NavigationState})
  public navigationState: NavigationState


  /**
   * the actual navigation element
   */
  private _navigationElement: HTMLElement;


  /**
   * get the navigation element
   */
  public get navigationElement(): HTMLElement {
    if (this._navigationElement) {
      return this._navigationElement;
    }
    return (this._navigationElement = this.navigation.assignedElements()[0] as HTMLElement);
  }


  /**
   * style definitions
   */
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

  public openNavigation(): void {
    this.changeNavigationState(NavigationState.Open, this.width);
  }

  public closeNavigation(): void {
    this.changeNavigationState(NavigationState.Closed, "0px");
  }


  public changeNavigationState(newState: NavigationState, width: string) : void {
    this.navigationState = newState;
    this.navigationElement.style.width = width;
    this.dispatchEvent(
        new CustomEvent(
            StandardApplicationLayout.NAVIGATION_STATE_CHANGED_EVENT, {
              detail: {
                state: this.navigationState
              }
            }));
  }


}