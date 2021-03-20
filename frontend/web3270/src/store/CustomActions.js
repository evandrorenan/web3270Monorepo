import * as actionTypes from "./actionTypes";
import * as websocketActions from "./connectionActions/websocketActions";

import { programResponse } from "../components/layout/mainContent/reports/programReport/programReportObjects";

// Functions exported on this class have to return an Action object.
// All actions has to have a type.

export const setStatus = (localStatus) => {
    return { 
        type: actionTypes.SET_STATUS,
        status : localStatus
    }
}

export const createRef = (localIndex, localRef) => {
    return { 
        type: actionTypes.CREATE_REF,
        index: localIndex,
        ref: localRef
    }
}

export const updatePositionText = (localIndex, localText) => {
    return { 
        type: actionTypes.UPDATE_POSITION_TEXT,
        index: localIndex,
        text: localText
    }
}

export const newSessionResponseHandler = (response) => {
    return newSessionAction(response.data.sessionId);
    // const x =  dispatch => {
    //     dispatch(newSessionAction(response.data.sessionId));
    //     dispatch(setStatus(STATUS_RETRIEVING_SCREEN));
    //     dispatch(getScreenFieldsAsync(response.data.sessionId));
    //     return newSessionAction(response.data.sessionId);
    // }
    // return x;
}

export const newSessionAction = (localSessionId) => {
    return {
        type: actionTypes.NEW_SESSION,
        sessionId: localSessionId,
        isConnecting: false
    };
};

export const getScreenResponseHandler = (response) => {
    return getScreenAction(response.data);
}

export const getScreenAction = (responseData) => {
    return {
        type: actionTypes.GET_SCREEN,
        fields: responseData.fields,
        fieldPos: responseData.fieldPos,
        cursorPos: responseData.cursorPos,
        sessionId : responseData.sessionId
    };
};

const buildRequestBody = (sendKeysParms) => {
    let requestBody = {};
    requestBody.sessionId = sendKeysParms.sessionId;
    requestBody.sendKeys = [];
    for (let i = 0; i < sendKeysParms.fields.length; i++) {
        if (sendKeysParms.fields[i].modified ) {
            if (!(sendKeysParms.fields[i].row === sendKeysParms.row 
            && sendKeysParms.fields[i].col === sendKeysParms.col )) {
                requestBody.sendKeys.push({
                    row : sendKeysParms.fields[i].row,
                    col : sendKeysParms.fields[i].col,
                    text : sendKeysParms.fields[i].ref.current.value.trimEnd(),
                    // text : ((fields[i].text + Array(fields[i].length).join(' ')).substr(0, fields[i].length)),
                    functionKey : ""
                })
            }
        }
    }
    
    requestBody.sendKeys.push({
        row : sendKeysParms.row,
        col : sendKeysParms.col,
        text : sendKeysParms.currentFieldText.trimEnd(),
        functionKey : sendKeysParms.userFunctionKey }); 
    return requestBody;
}

export const sendKeys = (stompClient, sendKeysParms) => {

    const requestBody = buildRequestBody(sendKeysParms);

    return dispatch => {
        dispatch(websocketActions.sendWebSocketMessage(requestBody, stompClient));
    };
};

export const setFieldText = (localIndex, localText) => {
    return { 
        type: actionTypes.SET_FIELD_TEXT,
        index: localIndex,
        text: localText 
    }
}

export const markModifiedField = (localIndex) => {
    return {
        type : actionTypes.MARK_MODIFIED_FIELD,
        index : localIndex
    }
}

export const setFocusedField = (localIndex, field) => {
    if (!localIndex || localIndex < 0 || !field)  {
        return { 
            type: actionTypes.DUMMY,
        }
    }

    return {
        type : actionTypes.SET_FOCUSED_FIELD,
        focusedField : field,
        index : localIndex
    }
}

export const setFocus = (field) => {
    return {
        type: actionTypes.SET_FOCUS,
        field: field
    }
}

export const requestAbendReport = (request) => {
    return requestAbendResponseHandler(programResponse);
}

export const requestAbendResponseHandler = (response) => {
    return {
        type: actionTypes.SET_REPORT_OBJECT,
        report: response.data
    }
}

export const setWaitingStatusTrue = () => {
    return {
        type: actionTypes.SET_WAITING_STATUS_TRUE
    }    
}

export const setWaitingStatusFalse = () => {
    return {
        type: actionTypes.SET_WAITING_STATUS_FALSE
    }    
}

export const downloadReport = (response) => {

    const contentType = 'application/octet-stream';
    const blob = new Blob([response.data], {type: contentType});
    let e = document.createEvent('MouseEvents');
    let a = document.createElement('a');

    const regex = /\d{2}\.\d{2}\.\d{2}\s(J.{2}\d{5})/gm;

    a.download = response.data.substr(response.data.search(regex) + 9, 8) + ".txt";
    a.href = window.URL.createObjectURL(blob);
    a.dataset.downloadurl =  [contentType, a.download, a.href].join(':');
    e.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
    a.dispatchEvent(e);

}