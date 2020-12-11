import { focusOn        } from "./InputFieldAction";
import { nextPosition   } from "./InputFieldInfo";
import { inputField     } from "./InputFieldInfo";

const HandleArrowKeys = (event, position, positions) => {

    let nextFocus;
    switch (event.key) {
        case "ArrowLeft":
            nextFocus = position === 0 ? 1919 : position - 1;
            break;
    
        case "ArrowRight":
            nextFocus = position === 1919 ? 0 : position + 1;
            break;

        case "ArrowUp":
            nextFocus = position - 80 + (position < 80 ? 1920 : 0);
            break;
    
        case "ArrowDown":
            nextFocus = position + 80 - (position >= 23 * 80 ? 1920 : 0);
            break;
    
        default:
            console.log("Invalid arrow key: " + event.key);
            break;
    }

    positions[nextFocus].ref.current.focus();
    return;
}

export default HandleArrowKeys;