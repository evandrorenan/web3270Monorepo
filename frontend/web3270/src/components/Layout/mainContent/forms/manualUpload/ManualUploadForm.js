import React, { Component } from 'react';

import '../Form.css';
import Input from '../Input';

class ManualUploadForm extends Component {

    constructor(props) {
        super();
        console.log("ManualUploadForm.props: " + JSON.stringify(props));

        this.state = { binaryFile: null };
        this.onclick = this.onclick.bind(this);
    }
    
    onclick(event) {
        this.props.ManualUploadForm({
            "Arquivo": this.state.form[0].value
        })
    }
    
    onchange(event) {        
        let localState = Object.assign({}, this.state)
        localState.binaryFile = event.target.value;
        this.setState(localState);
        return true;
    }

    render() {
        console.log("render: " + JSON.stringify(this.props));
        console.log("render: " + JSON.stringify(this.state));
        return (
        <div className="Form">
            <Input key="binary-file"   
                            type="file" 
                            label="Selecione o arquivo"
                            onChange={(event) => this.onchange(event)} />
            <input type="button" 
                   className="FormButton" 
                   disabled={this.props.waitingStatus} 
                   value="Upload" 
                   onClick={this.onclick} />
            {this.props.waitingStatus === true ? 
            <div className="Spinner" ></div> : <span /> }
        </div>
        )
    }
}

export default ManualUploadForm;