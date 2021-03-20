import * as actionTypes from "./actionTypes";

const initialFields = () => {
    let fields = [];
    for (let i = 0; i < 24; i++) {
        fields.push({
            fieldId : i,
            start : (i * 80) + 1,
            end: (i * 80) + 80,
            row: i + 1,
            col: i * 80 + 1,
            length : 80,
            text : " ",
            hidden : false,
            highLight : false,
            modified : false,
            ref : null 
        });
    }
    return fields;
}

const initialState = {  
    report: {  dataDivisionMap : [] },
    requestReport : { sourceCode : [], dataDivisionMap : [] },
    sessionId : null,
    screenId : null,
    fields: initialFields(),
    positions: [],
    fieldPos: [ 1 ],
    cursorPos : 1,
    focusedField : null,
    keyNameSufix : 1,
    isConnecting : false,
    isUpdatingScreen : false,
    status : "",
    stompClient : null,
    urlEvt : {},
    waitingStatus : false
}

const printScreenOnConsole = (fields) => {
    let localRow = "";
    for (let index = 0; index < fields.length; index++) {
        if ( index > 0 && fields[index-1].row !== fields[index].row) {
            console.log(localRow);
            localRow = fields[index].text;
        } else {
            localRow += fields[index].text;
        }
    }
    console.log(localRow);
}

const reducer = (state = initialState, action) => {
    if ( action.type === actionTypes.DUMMY ) {
        return state;
    }

    let newState = Object.assign({}, state);
    switch (action.type) {

        case actionTypes.NEW_SESSION:
            newState.sessionId = action.sessionId;
            newState.isConnecting = action.isConnecting;
            return newState;
        
        case actionTypes.GET_SCREEN:
            newState.fields = [];
            newState.fieldPos = action.fieldPos;
            newState.keyNameSufix = state.keyNameSufix === 1 ? 1920 : 1;
            newState.fields = action.fields;
            
            // for (let index = 0; index < action.fields.length; index++) {
            //     console.log('[reducer] action.fields[index]: ', action.fields[index].fieldId);                
            // }
            console.log('[reducer] action.fields.length: ', action.fields.length);
            newState.cursorPos = action.cursorPos;
            newState.sessionId = action.sessionId;
            printScreenOnConsole(newState.fields);
            return newState;

        case actionTypes.GET_SCREEN_FIELDS:
            newState.keyNameSufix = state.keyNameSufix === 1 ? 1920 : 1;
            newState.fields = action.fields;
            return newState;
    
        case actionTypes.SET_STATUS:
            newState.status = action.status;
            return newState;

        case actionTypes.CREATE_REF:
            newState.fields[action.index].ref = action.ref;
            return newState;

        case actionTypes.UPDATE_POSITION_TEXT:
            newState.fields[action.index].modified = true; 
            newState.fields[action.index].text = action.text; 
            return newState;

        case actionTypes.SET_FIELD_TEXT:
            newState.fields[action.index].text = action.text;
            return newState;

        case actionTypes.MARK_MODIFIED_FIELD:
            newState.fields[action.index].modified = true;
            return newState;

        case actionTypes.SET_FOCUSED_FIELD:
            newState.fields[action.index].focusedField = action.focusedField;
            return newState;    

        case actionTypes.SET_FOCUS:
            action.field.focus();
            return state;

        case actionTypes.SET_REPORT_OBJECT:
            newState.report = action.report;
            return newState;

        case actionTypes.SET_STOMP_CLIENT:
            newState.stompClient = action.stompClient;
            return newState;

        case actionTypes.SET_DOWNLOAD_OBJECT:
            newState.urlEvt = action.downloadURL;
            return newState;

        case actionTypes.SET_WAITING_STATUS_TRUE:
            newState.waitingStatus = true;
            return newState;
                
        case actionTypes.SET_WAITING_STATUS_FALSE:
            newState.waitingStatus = false;
            return newState;
                    
        default:
            break;
    }
    return state;
}

export default reducer;