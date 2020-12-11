import { isProtected }          from "./InputFieldInfo";
import { nextInputField }       from "./InputFieldInfo";
import { previowsInputField }   from "./InputFieldInfo";
import { nextPosition }         from "./InputFieldInfo";
import { inputField }           from "./InputFieldInfo";
import { deleteFieldValue }     from "./InputFieldAction";

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