import React, { Component } from 'react';
import Field                from './field/Field';
import { connect          } from 'react-redux';
import * as connectActions from '../../../../store/connectionActions/connectActions';

import './Screen.css';
import './field/Field.css';

class Screen extends Component {
    
    constructor(props){
        super();
        this.state = { initialConnect : false};
    }

    componentDidMount() {
        if (!this.state.initialConnect ) {
            this.setState({ initialConnect : true});     
            this.props.connectSession(null);
        }
    }

    shouldComponentUpdate() {
        return true;
    }

    connectionStatus () {
        return this.props.status;
    }

    render() {
        let rows = [24];

        for (let row = 0; row < 24; row++) {
            rows[row] = [];
            for (let index = 0; index < this.props.fields.length; index++) {
                if (this.props.fields[index].row === row + 1) {
                    rows[row].push(this.props.fields[index])
                }
            }
        }
        console.log('[Screen] this.props.fields.length: ', this.props.fields.length);

        return  (
            <div >
                <div className="Body">
                    <div className="Session">
                        <div className="Screen">
                            <div className="Screen" key="screen">
                                <div className="Rows">
                                    {rows.map((row, index) => {
                                        return <p className="Row" key={"row" + index} id={"row" + index}>
                                                    {rows[index].map((field, fieldIndex) => {
                                                        return <Field key={field.fieldId + "_" + this.props.keyNameSufix }
                                                                    id={"Field" + field.fieldId}
                                                                    field={field} /> }
                                                    )}
                                            </p>}
                                    )}
                                </div>
                            </div>
                        </div>
                        <div className="TraillerDiv">
                            <p className="Trailler">
                                    connectionStatus: {this.connectionStatus()}<br/>
                                    Session id: {this.props.sessionId}
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = state => {
    console.log('[Screen] state.fields.length: ', state.fields.length);
    return {
        sessionId: state.sessionId,
        fields: state.fields,
        keyNameSufix: state.keyNameSufix,
        isConnecting : state.isConnecting,
        isUpdatingScreen : state.isUpdatingScreen,
        status : state.status,
        focusedField: state.focusedField
    };
}

const mapDispatchToProps = dispatch => {
    return { 
        connectSession: (sessionId) => dispatch(connectActions.connectSession())
    }
}

// connect returns a function that receives State and Actions and pass it to Screen component via props.
export default connect(mapStateToProps, mapDispatchToProps)(Screen);