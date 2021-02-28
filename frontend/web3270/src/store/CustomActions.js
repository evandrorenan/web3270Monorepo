import axios                from 'axios';
import * as actionTypes from "./actionTypes";
import * as httpActions     from "./connectionActions/httpActions";
import * as websocketActions from "./connectionActions/websocketActions";

import { programResponse } from "../Session/ProgramReportObjects";

// Functions exported on this class have to return an Action object.
// All actions has to have a type.

const STATUS_RETRIEVING_SCREEN = "Retrieving screen data...";
const STATUS_READY = "Ready";
const STATUS_SENDING_INPUT_DATA = "Sending input data...";

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
        positions: responseData.positions,
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
    const payload = {
        "compilationJobid": request.compilationJobid,
        "user": request.user,
        "password": request.password,
        "programName": request.programName,
        "abendId": request.abendId,
        "abendFile": request.abendFile,
        "compilationReport": null, 
        "baseLocators": null
    }

    return requestAbendResponseHandler(programResponse);

    // return dispatch => {
    //     axios.post ("http://localhost:3000/programreport", payload)
    //         .then ( response => { 
    //             dispatch(requestAbendResponseHandler(response));
    //         });
    // };
}

export const requestAbendResponseHandler = (response) => {
    return {
        type: actionTypes.SET_REPORT_OBJECT,
        report: response.data
    }
}