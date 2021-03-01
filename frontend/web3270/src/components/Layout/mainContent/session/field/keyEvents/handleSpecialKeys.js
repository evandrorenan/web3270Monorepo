import { isProtected }          from "./inputFieldInfo";
import { nextInputField }       from "./inputFieldInfo";
import { previowsInputField }   from "./inputFieldInfo";
import { nextPosition }         from "./inputFieldInfo";
import { inputField }           from "./inputFieldInfo";
import { deleteFieldValue }     from "./inputFieldAction";

const HandleSpecialKeys = (event, position) => {

    if (event.key === "Home") {
        nextInputField(event, 0).focus();
        return;
    }

    if (event.key === "Delete") {
        deleteFieldValue(event, position);
        return false;
    }

    if (event.key === "Backspace") {
        handleBackspace(event, position);
        return false;
    }

    if (event.key === "Tab") {
        if (event.shiftKey) {
            previowsInputField(event, position).focus();
        } else {
            nextInputField(event, position).focus();
        }      
        return false;
    }

    return;
}

const handleBackspace = (event, position) => {
    
    if (isProtected(event.target)) {
        return false;
    } else {
        let previowsPos = nextPosition(position, -1);
        if (!isProtected(inputField(event, previowsPos))) {
            deleteFieldValue(event, previowsPos);
            inputField(event, previowsPos).focus();
        }
    }
}

export default HandleSpecialKeys;