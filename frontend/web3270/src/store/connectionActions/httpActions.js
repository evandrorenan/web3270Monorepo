import axios                from 'axios';
import * as customActions         from '../customActions';
import * as websocketActions from "./websocketActions";

// Functions exported on this class have to return an Action object.
// All actions has to have a type.

const STATUS_CONNECTING = "Connecting...";
const STATUS_READY = "Ready";

export const newSessionAsync = () => {
    const body = {
        "host": "127.0.0.1",
        "port": "3270"
    }
    return dispatch => {
        dispatch(customActions.setStatus(STATUS_CONNECTING));
        axios.post ("http://localhost:8080/newsession", body)
            .then ( response => { 
                dispatch(customActions.newSessionResponseHandler(response));
                dispatch(websocketActions.connectWebsocket());
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
        axios.get ("http://localhost:8080/session/" + sessionId + "/screen")
            .then ( response => { 
                dispatch(customActions.setStatus(STATUS_READY));
                dispatch(customActions.getScreenResponseHandler(response));
            } )
    };
};