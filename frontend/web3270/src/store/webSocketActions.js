import SockJS from 'sockjs-client';
import {Client} from '@stomp/stompjs';
import EventEmitter from 'eventemitter3';

const StompEventTypes = {
    Connect: 0,
    Disconnect: 1,
    Error: 2,
    WebSocketClose: 3,
    WebSocketError: 4,
}

export const newStompClient = (url) => {
    const stompEvent = new EventEmitter();

    let  _stompClient = new Client({
        brokerURL: 'ws:/localhost:3000/web3270-websocket',
        connectHeaders: {},
        debug: (str) => {
            console.log("[webSocketActions]:" + str)
        },
        reconnectDelay: 500,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        logRawCommunication: false,
        webSocketFactory: () => {
            return SockJS(url)
        },
        onStompError: (frame) => {
            stompEvent.emit(StompEventTypes.Error, frame)
        },
        onConnect: (frame) => {
            stompEvent.emit(StompEventTypes.Connect, frame)
        },
        onDisconnect: (frame) => {
            stompEvent.emit(StompEventTypes.Disconnect, frame)
        },
        onWebSocketClose: (frame) => {
            stompEvent.emit(StompEventTypes.WebSocketClose, frame)
        },
        onWebSocketError: (frame) => {
            stompEvent.emit(StompEventTypes.WebSocketError, frame)
        },
    })

    _stompClient.activate();

    return _stompClient
}