import axios                from 'axios';
import * as actionTypes from "../store/actionTypes";
import { Client } from '@stomp/stompjs';
import { programResponse } from "../components/layout/mainContent/reports/programReport/programReportObjects";
import { myStore } from '../../src/index';

// Functions exported on this file have to return an Action object.
// All actions has to have a type.

const STATUS_CONNECTING = "Connecting...";
const STATUS_CONNECTING_WEBSOCKET = "Connecting Websocket...";
const STATUS_RETRIEVING_SCREEN = "Retrieving screen data...";
const STATUS_READY = "Ready";
const STATUS_SENDING_INPUT_DATA = "Sending input data...";

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

export const newSessionAsync = () => {
    const body = {
        "host": "192.168.240.1",
        "port": "51004"
    }
    return dispatch => {
        dispatch(setStatus(STATUS_CONNECTING));
        axios.post ("http://10.4.66.22:3000/newsession", body)
            .then ( response => { 
                dispatch(newSessionResponseHandler(response));
            });
    };
};

export const setStatus = (localStatus) => {
    return { 
        type: actionTypes.SET_STATUS,
        status : localStatus
    }
}

const newSessionResponseHandler = (response) => {
    return dispatch => {
        dispatch(newSessionAction(response.data.sessionId));
        dispatch(connectScreenWebsocket(response.data.sessionId));
        dispatch(setStatus(STATUS_RETRIEVING_SCREEN));
        dispatch(getScreenFieldsAsync(response.data.sessionId));
    }
}

export const newSessionAction = (localSessionId) => {
    return {
        type: actionTypes.NEW_SESSION,
        sessionId: localSessionId,
        isConnecting: false
    };
};

const connectScreenWebsocket = (sessionId) => {
    let stompClient;

    const onMessage = (message) => {
        let response = {};
        response.data = JSON.parse(message.body);
        myStore.dispatch(getScreenAction(response.data));
    }

    const wsConnectSuccess = (frame) => {
        stompClient.subscribe('/queue/screens/' + sessionId, 
                              message => onMessage (message)); 
    }    

    const stompConfig = {
        connectHeaders: {},
        brokerURL: "ws://10.4.66.22:3000/web3270-websocket",
        
        // Keep it off for production, it can be quit verbose
        // Skip this key to disable
        debug: function (str) {
          console.log('STOMP: ' + str);
        },
        // If disconnected, it will retry after 200ms
        reconnectDelay: 200,
          
        // Subscriptions should be done inside onConnect as those need to reinstated when the broker reconnects
        onConnect: (frame) => wsConnectSuccess(frame)
    }
    
    // Create an instance
    stompClient = new Client(stompConfig);

    // You can set additional configuration here

    // Attempt to connect
    stompClient.activate();

    return { 
        type: actionTypes.SET_STATUS,
        status : STATUS_CONNECTING_WEBSOCKET
    }
}

export const getScreenFromWebSocket = (message) => {
    let response = {};
    response.data = JSON.parse(message.body);
    getScreenAction(response.data);
}

export const getScreenAsync = (sessionId) => {
    if (!sessionId) {
        return dispatch => {
            dispatch(newSessionAsync());
        }
    }
    return dispatch => {
        axios.get ("http://10.4.66.22:3000/session/" + sessionId + "/screen")
            .then ( response => { 
                dispatch(setStatus(STATUS_READY));
                dispatch(getScreenResponseHandler(response));
            } )
    };
};

export const getScreenFieldsAsync = (sessionId) => {
    if (!sessionId) {
        return dispatch => {
            dispatch(newSessionAsync());
        }
    }
    return dispatch => {
        axios.get ("http://10.4.66.22:3000/session/" + sessionId + "/screenfields")
            .then ( response => { 
                dispatch(setStatus(STATUS_READY));
                dispatch(getScreenResponseHandler(response));
            } )
    };
};

const getScreenResponseHandler = (response) => {
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

const buildRequestBody = (row, col, currentFieldText, userFunctionKey, fields, sessionId) => {
    let requestBody = {};
    requestBody.sessionId = sessionId;
    requestBody.sendKeys = [];
    for (let i = 0; i < fields.length; i++) {
        if (fields[i].modified ) {
            if (!(fields[i].row === row && fields[i].col === col )) {
                requestBody.sendKeys.push({
                    row : fields[i].row,
                    col : fields[i].col,
                    text : fields[i].ref.current.value.trimEnd(),
                    // text : ((fields[i].text + Array(fields[i].length).join(' ')).substr(0, fields[i].length)),
                    functionKey : ""
                })
            }
        }
    }
    
    requestBody.sendKeys.push({
        row : row,
        col : col,
        text : currentFieldText.trimEnd(),
        // text : ((currentFieldText + Array(currentFieldLen).join(' ')).substr(0, currentFieldLen)),
        functionKey : userFunctionKey }); 
    return requestBody;
}

export const sendKeys = (row, col, currentFieldText, functionKey, fields, sessionId) => {

    const requestBody = buildRequestBody(row, col, currentFieldText, functionKey, fields, sessionId);

    return dispatch => {
        dispatch(setStatus(STATUS_SENDING_INPUT_DATA));
        axios.post ("http://10.4.66.22:3000/ws/sendkeys", requestBody)
        .then ( response => { 
                dispatch(setStatus(STATUS_READY));
            });
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