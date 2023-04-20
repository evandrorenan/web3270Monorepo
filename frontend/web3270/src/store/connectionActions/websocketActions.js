import axios                from 'axios';
import * as customActions   from '../CustomActions';
import SockJS               from 'sockjs-client';
import Stomp                from 'stompjs';
import * as actionTypes     from '../actionTypes';

const STATUS_CONNECTING = "Connecting...";
const STATUS_READY = "Ready";

export const connectWebsocket = () => {
    let socket = new SockJS('http://10.4.66.22:3000/web3270-websocket');
    let localStompClient = Stomp.over(socket);
    localStompClient.connect ({}, function (message) {
        localStompClient.subscribe('/topic/screens', function (message) {
            customActions.getScreenResponseHandler(message);
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
                dispatch(connectWebsocket());
            });
    };
};

export const getScreenAsync = (sessionId) => {
    if (!sessionId) {
        return dispatch => {
            dispatch(newSessionAsync());
        }
    }
    return dispatch => {
        axios.get ("http://10.4.66.22:3000/session/" + sessionId + "/screen")
            .then ( response => { 
                dispatch(customActions.setStatus(STATUS_READY));
                dispatch(customActions.getScreenResponseHandler(response));
            } )
    };
};