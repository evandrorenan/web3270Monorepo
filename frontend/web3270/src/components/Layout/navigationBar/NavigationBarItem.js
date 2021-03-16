import React from 'react';

import './NavigationBar.css';

const NavigationBarItem = (props) => {
    return (
        <button id={props.id} 
                key={"TabItem" + props.id} 
                className={"button " + (props.isActive ? " active" : "")}  
                onClick={props.onclick}>{props.name}
        </button>
    )
}

export default NavigationBarItem;