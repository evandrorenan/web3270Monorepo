import axios                from 'axios';
import * as customActions   from '../CustomActions';
import SockJS               from 'sockjs-client';
import Stomp                from 'stompjs';
import * as actionTypes     from '../actionTypes';
import {myStore}            from '../../index';

const STATUS_READY = "Ready";
const STATUS_CONNECTING = "Connecting...";
const STATUS_CONNECTING_WEBSOCKET = "Connecting Websocket...";

export const connectSession = () => {
    return dispatch => {
        dispatch ( newSessionAsync() );
    }
}

export const connectWebsocket = (sessionId) => {
    myStore.dispatch (customActions.setStatus(STATUS_CONNECTING_WEBSOCKET));

    let socket = new SockJS('http://10.4.66.22:3000/web3270-websocket');
    let localStompClient = Stomp.over(socket);
    localStompClient.connect ({}, function (message) {
        myStore.dispatch (customActions.setStatus(STATUS_READY));
        localStompClient.subscribe('/queue/session/' + sessionId , function (message) {
            myStore.dispatch (customActions.getScreenAction(JSON.parse(message.body)));
        });        
    });
    return {
        type: actionTypes.SET_STOMP_CLIENT,
        stompClient: localStompClient
    };
}

export const disconnectWebSocket = (stompClient) => {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    return {
        type: actionTypes.SET_STOMP_CLIENT,
        stompClient: null
    };
}

export const sendWebSocketMessage = (payload, stompClient) => {

    stompClient.send("/ws/sendkeys", {}, JSON.stringify(payload));

    return {
        type: actionTypes.DUMMY
    }
}

export const newSessionAsync = () => {
    const body = {
        "host": "192.168.240.1",
        "port": "51004"
    }
    return dispatch => {
        dispatch(customActions.setStatus(STATUS_CONNECTING));
        axios.post ("http://10.4.66.22:3000/newsession", body)
            .then ( response => { 
                dispatch(customActions.newSessionResponseHandler(response));
                dispatch(getScreenFieldsAsync(response.data.sessionId));
                dispatch(connectWebsocket(response.data.sessionId));
            });
    };
};

export const getScreenFieldsAsync = (sessionId) => {
    return dispatch => {
        axios.get ("http://10.4.66.22:3000/session/" + sessionId + "/screenfields")
            .then ( response => { 
                // dispatch(customActions.setStatus(STATUS_WAITING_WEBSOCKET));
                dispatch(customActions.getScreenResponseHandler(response));
            } )
    };
};

export const requestSysoutEvt = (request) => {
    
    return dispatch => {
        dispatch(customActions.setWaitingStatusTrue());
        axios.get ("http://10.4.66.22:3000/sysout/txt/" 
                  + request.evt    + "/" 
                  + request.option + "/" 
                  + request.jobId )
            .then ( response => { 
                dispatch(customActions.setWaitingStatusFalse());
                customActions.downloadReport(response);
            } )
            .catch ( err => {
                dispatch(customActions.setWaitingStatusFalse());
                console.log('[reducer] err: ', err);
            })
    };
};