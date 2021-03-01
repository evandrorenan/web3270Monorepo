import { nextPosition }     from "./inputFieldInfo";
import { isProtected }      from "./inputFieldInfo";
import { inputField }       from "./inputFieldInfo";
import { nextInputField }   from "./inputFieldInfo";
import { getPositionNumber }      from "./inputFieldInfo";
import { focusOn }          from "./inputFieldAction";

const HandleType = (event, position) => {
    position = nextPosition(position, 1);

    event.target.value = event.key;
    if (isProtected(inputField(event, getPositionNumber(event.target) + 1))) {
        nextInputField(event, position).focus();
        return;
    } else {
        focusOn(event, position);
    }
}

export default HandleType;