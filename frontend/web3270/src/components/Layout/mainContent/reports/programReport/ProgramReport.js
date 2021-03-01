import React, { Component } from 'react';
import { connect          } from 'react-redux';

import './ProgramReport.css';

class ProgramReport extends Component {

    constructor(props) {
        super();
    }

    render() {
        return (<div className="SourceDiv" key="SourceDiv">
                { typeof this.props.report.sourceCode === 'undefined' ? "" :
                    this.props.report.sourceCode.map((line) => {
                    return <div className="ProgramLine" key={"line" + line.lineId}>
                            <div className="var" key={"var" + line.lineId}/>
                            <div className="SourceCodeLine" key={"sourceCodeLine" + line.lineId}>
                                {line.text}
                            </div>
                    </div>
                })}
        </div>);
    }
}

const mapStateToProps = (state) => {
    return { report : state.report };
}

export default connect(mapStateToProps)(ProgramReport);