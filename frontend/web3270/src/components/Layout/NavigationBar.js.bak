import React, { Component } from 'react';

import TabItem from './TabItem';

class NavigationBar extends Component {

    constructor(props) {
        super();
        this.state = props.state;
    }

    render() {
        return (
            <div className="NavigationBar" key="NavigationBar">
                {this.state.items.map((item) => {
                    return <TabItem id={item.id} name={item.name} isActive={item.isActive} onclick={this.props.onclick} />
                })}
            </div>
        );
    }
}

export default NavigationBar;