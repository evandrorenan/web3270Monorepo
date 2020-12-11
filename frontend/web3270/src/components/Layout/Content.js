import React  from 'react';

import './Content.css';
import Screen from '../../Session/Screen';
import ProgramReport from '../../Session/ProgramReport';
import DataDivisionMap from '../../Session/DataDivisionMap';
import RequestReport from '../Layout/Forms/RequestReportForm/RequestReportForm';

const Content = (props) => {
    return (
        <div className="Content">
            <span class={props.activeTab() === "RequestReport" ? "NotHiddenDiv" : "HiddenDiv"}>
                <RequestReport />
            </span>
            <span class={props.activeTab() === "SessionItem" ? "NotHiddenDiv" : "HiddenDiv"}>
                <Screen onkeyup={onkeyup} onkeydown={onkeydown}  />
            </span>
            <span class={props.activeTab() === "Report" ? "NotHiddenDiv" : "HiddenDiv"}>
                <div class="report">
                    <ProgramReport />
                    <DataDivisionMap />
                </div> 
            </span>
        </div>)
}

export default Content;