import React from 'react';

import './input.css';

const input = (props) => {
    let inputElement = <input className="InputElement" {...props} />;

    return (
        <div className="Input"> 
            <label className="Label" >{props.label}</label>
            {inputElement}
        </div>
    )
}

export default input;