import React from 'react';

import './NavigationBar.css';

const TabItem = (props) => {
    console.log('TabItem:' + props.id)
    return (
        <button id={props.id} 
                key={"TabItem" + props.id} 
                className={"button " + (props.isActive ? " active" : "")}  
                onClick={props.onclick}>{props.name}
        </button>
    )
}

export default TabItem;