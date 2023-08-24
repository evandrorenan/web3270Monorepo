import React from 'react';

import './Input.css';

const Input = (props) => {
    let inputElement = <input className="InputElement" {...props} />;

    return (
        <div className="Input"> 
            <label className="Label" >{props.label}</label>
            {inputElement}
        </div>
    )
}

export default Input;