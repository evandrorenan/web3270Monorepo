import React  from 'react';

import './Content.css';
import Screen from './session/Screen';
import ProgramReport from './reports/programReport/ProgramReport';
import DataDivisionMap from './reports/dataDivisionMap/DataDivisionMap';
import RequestReport from './forms/requestReportForm/RequestReportForm';

const Content = (props) => {
    return (
        <div className="Content">
            <span className={props.activeTab() === "RequestReport" ? "NotHiddenDiv" : "HiddenDiv"}>
                <RequestReport />
            </span>
            <span className={props.activeTab() === "SessionItem" ? "NotHiddenDiv" : "HiddenDiv"}>
                <Screen />
            </span>
            <span className={props.activeTab() === "Report" ? "NotHiddenDiv" : "HiddenDiv"}>
                <div className="report">
                    <ProgramReport />
                    <DataDivisionMap />
                </div> 
            </span>
        </div>)
}

export default Content;