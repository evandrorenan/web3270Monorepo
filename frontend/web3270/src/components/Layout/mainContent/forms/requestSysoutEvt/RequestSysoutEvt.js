import React, { Component } from 'react';
import { connect          } from 'react-redux';

import '../form.css';
import Input from '../requestReportForm/input';

import * as connectionActions  from '../../../../../store/connectionActions/connectActions';

class RequestSysoutEvt extends Component {

    constructor(props) {
        super();
        this.state = { form: [
            {id: 0, label: "EVT (evt04, evts05...)",    value:"", maxLength: "6" },
            {id: 1, label: "Usuario",                   value:"", maxLength: "8" },
            {id: 2, label: "Senha",                     value:"", maxLength: "8" },
            {id: 3, label: "Opcao (Z ou C)",            value:"", maxLength: "1" },
            {id: 4, label: "Job id",                    value:"", maxLength: "7" },
        ]};

        this.onclick = this.onclick.bind(this);
    }
    
    onclick(event) {
        this.props.requestSysoutEvt({
            "evt":      this.state.form[0].value,
            "user":     this.state.form[1].value,
            "password": this.state.form[2].value,
            "option":   this.state.form[3].value,
            "jobId":    this.state.form[4].value
        })
    }
    
    onchange(event, id) {        
        let localState = Object.assign({}, this.state)
        localState.form[id].value = event.target.value;
        this.setState(localState);
        return true;
    }

    render() {
        return (
        <div className="Form">
            { this.state.form.map((item, index) => {
                return <Input key={item.label + index} 
                              type="text" 
                              label={item.label} 
                              maxLength={item.maxLength} 
                              onChange={(event) => this.onchange(event, item.id)} />
            })}
            <input type="button" 
                   className="FormButton" 
                   disabled={this.props.waitingStatus} 
                   value="Request Report" 
                   onClick={this.onclick} />
            {this.props.waitingStatus === true ? 
            <div className="Spinner" ></div> : <span /> }
        </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        requestReport: state.requestReport,
        urlEvt: state.urlEvt,
        waitingStatus : state.waitingStatus
    };
}

const mapDispatchToProps = dispatch => {
    return { 
        requestSysoutEvt: (request) => dispatch(connectionActions.requestSysoutEvt(request))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(RequestSysoutEvt);