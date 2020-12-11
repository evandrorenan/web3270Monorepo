import React, {PureComponent}     from 'react';
import { isProtected }        from "../KeyEvents/InputFieldInfo";
import { isTypedChar }        from "../KeyEvents/KeyTipe";
import SessionContext         from "../context/sessionContext";

import './Position.css'

class Position2 extends PureComponent {

     // positionAttributes sample:
     // {"positionId":  0, "text": "C", "protected": false, "hidden": false, "highLight": false }

     constructor(props){
          super();
          if (props.id === "Position1") {
               console.log ("constructor key: " + props.id);
          }

          this.positionRef = React.createRef();
          this.focusPositionRef = this.focusPositionRef.bind(this);

          this.state = { positionAttributes :           
                         { "positionId": props.colNumber, 
                           "text": " ", 
                           "protected": false,
                           "hidden": false, 
                           "highLight": false }};

          if (props.position) {
               this.state = { positionAttributes: props.position };
          }

          this.state.className = "Position ";
          this.state.className += this.state.positionAttributes.protected ? " Prot-" : " NotProt-";

          if (this.state.positionAttributes.hidden) {
               this.state.className += "Hidden";
          } else {
               this.state.className += this.state.positionAttributes.highLight ? "High" : "NotHigh";
          }       
     }

     focusPositionRef() {
          this.positionRef.current.focus();
     }

     onchange = (event) => {
          console.log("handleFocus");
     }
     onkeydown = (event) => {
          return false;
     }     
     
     onkeydown = (event) => {
          let returnValue = this.props.onkeydown(event);

          const localState = {...this.state};

          if (isTypedChar(event) 
          && !isProtected(event.target) 
          && event.target.className.search(" Mod") < 0) {
               localState.className += " Mod";
               localState.positionAttributes.text = event.key;
               this.setState(localState);               
          }

          event.preventDefault();
          return returnValue;
     }

     render() {
         return ( 
               <input 
                    key={"Position" + this.props.id}
                    id={"Position" + this.state.positionAttributes.positionId}
                    type="text" 
                    ref={this.positionRef}
                    className={this.state.className}
                    maxLength="1" 
                    onKeyDown={this.onkeydown}
                    onKeyUp={this.onkeyup}
                    onChange={this.onchange}
                    value={this.state.positionAttributes.text} />
          )    
     }
}

Position2.contextType = SessionContext;

export default Position2;