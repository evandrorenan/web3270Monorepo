import React from 'react';

import './Row.css';
import Position from './Position.js';

const Row = (props) => {
    let cols = props.buffer.split('');
    let keyName = "line" + props.rowNumber;

    return ( 
        <p className="Row" key={keyName}>
            {cols.map((position, index) => {
                return <Position 
                    key={index}
                    fieldList={props.fieldList}
                    content={position} 
                    onkeyup={props.onkeyup}
                    onKeyDown={props.onkeydown}
                    rowNumber={props.rowNumber} 
                    colNumber={index} />
                })
            }
        </p>
    )
}

export default Row;