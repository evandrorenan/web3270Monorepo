import React, {Component} from 'react';
import { isFunctionKey, isTypedChar }        from "./keyEvents/keyType";
import { connect }            from 'react-redux';
import * as customActions     from "../../../../../store/CustomActions";
import { getPfkey }           from './keyEvents/handleFunctionKey';

import './Field.css';

class Field extends Component {

     constructor(props){
          super();
          this.state = { currentField : props.field,
                         insertMode : false }
          let ref = React.createRef();
          props.createRef(
               props.fields.findIndex(field => field.fieldId === props.field.fieldId),
               ref);
     }

     rcPosition() {
          return (this.state.currentField.row - 1) * 80 + this.state.currentField.col;
     }

     markModified = () => {
          let localState = this.state;
          if (!localState.currentField.modified) {
               localState.currentField.modified = true;
               this.setState(localState);
               this.props.markModifiedField(
                    this.props.fields.findIndex(
                         field => field.fieldId === localState.currentField.fieldId));
          }
     }

     getClassName = (field) => {
          let className = "Field";

          // className += field.protected ? " Prot-" : " NotProt-";

          if (field.hidden) {
               className += " Hidden";
          } else {
               className += " " + field.color;
          }

          return className;     
     }

     focusNextField = (currentPosition) => {
          
          let first = this.props.fields[0].ref.current;
          let firstUnprot;

          for (let i = 0; i < this.props.fields.length; i++) {
               if (!this.props.fields[i].protected ) {
                    if (typeof firstUnprot === "undefined") {
                         firstUnprot = this.props.fields[i].ref.current;
                    }
                    if ( this.props.fields[i].start > currentPosition ) {
                         this.props.setFocus(this.props.fields[i].ref.current);
                         return;
                    }
               }
          }

          if (typeof firstUnprot !== "undefined") {
               if (firstUnprot !== this.state.currentField.ref.current) {
                    this.props.setFocus(firstUnprot);
               }
          } else {
               this.props.setFocus(first);
          }
     }

     focusPreviousField = (currentPosition) => {
          
          let last = this.props.fields[this.props.fields.length -1].ref.current;
          let lastUnprot;

          for (let i = this.props.fields.length -1; i > -1; i--) {
               if (!this.props.fields[i].protected ) {
                    if (typeof lastUnprot === "undefined") {
                         lastUnprot = this.props.fields[i].ref.current;
                    }
                    if ( this.props.fields[i].start < currentPosition ) {
                         this.props.setFocus(this.props.fields[i].ref.current);
                         return;
                    }
               }
          }

          if (typeof lastUnprot !== "undefined") {
               if (lastUnprot !== this.state.currentField.ref.current) {
                    this.props.setFocus(lastUnprot);
               }
          } else {
               this.props.setFocus(last);
          }
     }

     shouldComponentUpdate(nextProps, nextState) {
          return true;
     }

     componentDidMount() {
          if (this.props.cursorPos === this.rcPosition()){
               if (this.state.currentField.ref.current) {
                    this.state.currentField.ref.current.focus();
                    return;
               }

          }
     }

     onfocus = (event) => {
          event.target.selectionStart = 0;
          event.target.selectionEnd = 0;
     }

     onchange = (event) => {
          if (isTypedChar(event)) {
               this.markModified();
          }
     }

     onkeydown = (event) => {
          if (isFunctionKey(event)) {
               this.markModified();
               let sendKeysParms = {
                    row : this.state.currentField.row,
                    col : this.state.currentField.col,
                    currentFieldText : event.target.value,
                    userFunctionKey : getPfkey(event),
                    fields : this.props.fields,
                    sessionId : this.props.sessionId,
               };
               this.props.sendKeys(this.props.stompClient, sendKeysParms);
               event.preventDefault();
               return false;
          } 
          if (isTypedChar(event)) {
               if (this.state.currentField.protected) {
                    event.preventDefault();
                    return;
               }
               this.markModified();
               event.target.selectionEnd = event.target.selectionStart + 1;
          }
          if (event.key === "Home") {
               this.focusNextField(0);       
          }
          if (event.key === "Tab") {
               if (event.shiftKey) {
                    this.focusPreviousField(this.state.currentField.start);
                    event.preventDefault();
                } else {
                    this.focusNextField(this.state.currentField.start);
                    event.preventDefault();
                }      
          }
          if (event.key === "ArrowUp" ) {
               this.findField(
                    this.state.currentField.row === 1 ? 24 : this.state.currentField.row - 1,
                    this.state.currentField.col + event.target.selectionStart);
               event.preventDefault();
          } 
          if (event.key === "ArrowDown" ) {
               this.findField(
                    this.state.currentField.row === 24 ? 1 : this.state.currentField.row + 1,
                    this.state.currentField.col + event.target.selectionStart);
               event.preventDefault();
          } 
     }

     findField = (row, column) => {
          for (let i = 0; i < this.props.fields.length; i++) {
               let field = this.props.fields[i];
               if ( field.row === row) {
                    if ( field.col <= column
                    &&   field.col >= column - field.length) {
                         field.ref.current.focus();
                         field.ref.current.selectionStart = column - field.col;
                         field.ref.current.selectionEnd = field.ref.current.selectionStart;
                    }
               }
          }
     }

     onkeyup = (event) => {
          if (isTypedChar(event)
          && event.target.maxLength === event.target.selectionStart) {
               this.focusNextField(this.state.currentField.start);
          }     
     }

     render() {      

          console.log('[Field] this.state.currentField.fieldId: ', this.state.currentField.fieldId);

          return ( 
               <input 
                    key={"Position" + this.state.currentField.fieldId + this.state.sufix}
                    id={"Position" + this.state.currentField.fieldId}
                    ref={this.state.currentField.ref}
                    className={this.getClassName(this.state.currentField) + (this.state.currentField.modified === true ? " Modified" : "")}
                    defaultValue={this.state.currentField.text}
                    type="text"
                    onFocus={this.onfocus}
                    onChange={this.onchange}
                    // disabled={this.state.currentField.protected}
                    onKeyUp={this.onkeyup}
                    onKeyDown={this.onkeydown}
                    style={{width: 11.7875 * (this.state.currentField.length)}}
                    maxLength={(this.state.currentField.length)}
               />
          )    
     }
}

const mapStateToProps = state => {
     return {
          stompClient : state.stompClient,
          fields: state.fields,
          sessionId: state.sessionId,
          cursorPos: state.cursorPos,
          fieldPos: state.fieldPos
     };
}
 
const mapDispatchToProps = dispatch => {
     return { 
         createRef: (index, ref) => dispatch(customActions.createRef(index, ref)),
         sendKeys: (stompClient, sendKeysParms) => 
                    dispatch(customActions.sendKeys(stompClient, sendKeysParms)),
         setFieldText: (index, text) => dispatch(customActions.setFieldText(index, text)),
         markModifiedField: (index) => dispatch(customActions.markModifiedField(index)),
         setFocus: (field) => dispatch(customActions.setFocus(field))
     }
 }

export default connect(mapStateToProps, mapDispatchToProps)(Field);