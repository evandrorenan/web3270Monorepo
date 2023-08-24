import React  from 'react';

import './Content.css';
import Screen from './session/Screen';
import ProgramReport from './reports/programReport/ProgramReport';
import DataDivisionMap from './reports/dataDivisionMap/DataDivisionMap';
import RequestSysoutEvt from './forms/requestSysoutEvt/RequestSysoutEvt';
import ManualUploadForm from './forms/manualUpload/ManualUploadForm';
// import RequestReport from './forms/requestReportForm/RequestReportForm';

const Content = (props) => {
    return (
        <div className="Content">
            <span className={props.activeTab() === "RequestSysoutEvt" ? "NotHiddenDiv" : "HiddenDiv"}>
                <RequestSysoutEvt />
            </span>
            <span className={props.activeTab() === "SessionItem" ? "NotHiddenDiv" : "HiddenDiv"}>
                <Screen />
            </span>
            <span className={props.activeTab() === "ManualUploadForm" ? "NotHiddenDiv" : "HiddenDiv"}>
                <ManualUploadForm />
            </span>
            <span className={props.activeTab() === "Report" ? "NotHiddenDiv" : "HiddenDiv"}>
                <div className="Report">
                    <ProgramReport />
                    <DataDivisionMap />
                </div> 
            </span>
        </div>)
}

export default Content;