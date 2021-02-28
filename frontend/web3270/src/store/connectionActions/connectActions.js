import axios                from 'axios';
import * as customActions         from '../CustomActions';
import * as actions         from '../CustomActions';
import SockJS               from 'sockjs-client';
import Stomp                from 'stompjs';
import * as actionTypes     from '../actionTypes';

const STATUS_CONNECTING = "Connecting...";
const STATUS_READY = "Ready";

export const connectSession = () => {
    return dispatch => {
        dispatch ( newSessionAsync() );
    }
}

export const connectWebsocket = (sessionId) => {
    let socket = new SockJS('http://localhost:3000/web3270-websocket');
    let localStompClient = Stomp.over(socket);
    console.log("StompClient instanciado");
    localStompClient.connect ({}, function (message) {
        console.log("Websocket connected");
        console.log('subscribing: /queue/session/' + sessionId);
        localStompClient.subscribe('/queue/session/' + sessionId , function (message) {
            console.log('new message. Body: ', message.body);
            actions.getScreenResponseHandler(message);
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
    console.log("Websocket disconnected");
    return {
        type: actionTypes.SET_STOMP_CLIENT,
        stompClient: null
    };
}

export const sendWebSocketMessage = (payload, stompClient) => {

    stompClient.send("/ws/sendkeys", {}, JSON.stringify(payload));
        //     "sessionId": "20201117192908268",
        //     "sendKeys": [
        //         {
        //             "row": 24,
        //             "col": 29,
        //             "text": "ACCTER",
        //             "functionKey": "[enter]"
        //         }    
        //     ]})
        // );

    console.log('payload: ', payload);

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
        axios.post ("http://localhost:3000/newsession", body)
            .then ( response => { 
                dispatch(customActions.newSessionResponseHandler(response));
                dispatch(getScreenFieldsAsync(response.data.sessionId));
                dispatch(connectWebsocket(response.data.sessionId));
            });
    };
};

export const getScreenFieldsAsync = (sessionId) => {
    return dispatch => {
        axios.get ("http://localhost:3000/session/" + sessionId + "/screenfields")
            .then ( response => { 
                dispatch(customActions.setStatus(STATUS_READY));
                dispatch(customActions.getScreenResponseHandler(response));
            } )
    };
};