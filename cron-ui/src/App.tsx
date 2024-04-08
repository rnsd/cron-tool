import './App.css';
import React, {Component} from 'react';
import {Col, Form, Input, Row} from "antd";
import axios from 'axios';
import CronExplanation from "./components/explanation/CronExplanation";
import {CronSchedule} from "./model/CronSchedule";
import InputFieldHints from "./components/hints/InputFieldHints";

interface IState {
    cronSchedule: CronSchedule;
    focusedField: string;
}

class App extends Component<{}, IState> {

    constructor(props: any) {
        super(props);
        this.state = {
            cronSchedule: {
                minute: { expression: "*", errorMessage: null },
                hour: { expression: "*", errorMessage: null },
                dayOfMonth: { expression: "*", errorMessage: null },
                month: { expression: "*", errorMessage: null },
                dayOfWeek: { expression: "*", errorMessage: null },
                scheduleExplanation: "At every minute."
            },
           focusedField: ''
        };
    }

    handleInputChange = async (key: keyof CronSchedule, newValue: string) => {
        const newCronSchedule = {...this.state.cronSchedule, [key]: {...this.state.cronSchedule[key], expression: newValue}};
        const res = await axios.get(
            'http://localhost:8081/cron/evaluate',
            {
                params: {
                    minute: newCronSchedule.minute.expression,
                    hour: newCronSchedule.hour.expression,
                    dayOfMonth: newCronSchedule.dayOfMonth.expression,
                    month: newCronSchedule.month.expression,
                    dayOfWeek: newCronSchedule.dayOfWeek.expression
                }
            });
        this.setState({
            ...this.state,
            cronSchedule: res.data
        });
    }


    // function to switch focus
    switchFocusFun = (fieldName: string) => {
        this.setState({focusedField: fieldName});
    }

    render() {
        return (
            <div className="App">
                <div style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    height: '100vh'
                }}>
                    <CronExplanation scheduleExplanation={this.state.cronSchedule.scheduleExplanation}/>

                    <Form>
                        <Row gutter={16} justify='center' align='middle'>
                            <Col>
                                <Form.Item
                                    name='Minute'
                                    rules={[
                                        {
                                            pattern: new RegExp(/^[\d*,\/,-]+$/),
                                            message: 'Invalid input!',
                                        }
                                    ]}
                                    help={this.state.cronSchedule.minute.errorMessage ? this.state.cronSchedule.minute.errorMessage : ''}
                                    validateStatus={this.state.cronSchedule.minute.errorMessage ? 'error' : ''}
                                >
                                    <div>
                                        <label className="labelStyle">Minute</label>
                                        <Input className="inputStyle" value={this.state.cronSchedule.minute.expression}
                                               onChange={(e) => this.handleInputChange('minute', e.target.value)}
                                               onFocus={(e) => this.switchFocusFun('minute')}
                                        />
                                    </div>
                                </Form.Item>
                            </Col>
                            <Col>
                                <Form.Item
                                    name='Hour'
                                    rules={[
                                        {
                                            pattern: new RegExp(/^[\d*,\/,-]+$/),
                                            message: 'Invalid input!',
                                        }
                                    ]}
                                >
                                    <div>
                                        <label className="labelStyle">Hour</label>
                                        <Input className="inputStyle" value={this.state.cronSchedule.hour.expression}
                                               onChange={(e) => this.handleInputChange('hour', e.target.value)}
                                               onFocus={(e) => this.switchFocusFun('hour')}
                                        />
                                    </div>
                                </Form.Item>
                            </Col>
                            <Col>
                                <Form.Item
                                    name='DayOfMonth'
                                    rules={[
                                        {
                                            pattern: new RegExp(/^[\d*,\/,-]+$/),
                                            message: 'Invalid input!',
                                        }
                                    ]}
                                >
                                    <div>
                                        <label className="labelStyle">Day-of-Month</label>
                                        <Input className="inputStyle" value={this.state.cronSchedule.dayOfMonth.expression}
                                               onChange={(e) => this.handleInputChange('dayOfMonth', e.target.value)}
                                               onFocus={(e) => this.switchFocusFun('dayOfMonth')}
                                        />
                                    </div>
                                </Form.Item>
                            </Col>
                            <Col>
                                <Form.Item
                                    name='Month'
                                    rules={[
                                        {
                                            pattern: new RegExp(/^[\d*,\/,-]+$/),
                                            message: 'Invalid input!',
                                        }
                                    ]}
                                >
                                    <div>
                                        <label className="labelStyle">Month</label>
                                        <Input className="inputStyle" value={this.state.cronSchedule.month.expression}
                                               onChange={(e) => this.handleInputChange('month', e.target.value)}
                                               onFocus={(e) => this.switchFocusFun('month')}
                                        />

                                    </div>
                                </Form.Item>
                            </Col>
                            <Col>
                                <Form.Item
                                    name='DayOfWeek'
                                    rules={[
                                        {
                                            pattern: new RegExp(/^[\d*,\/,-]+$/),
                                            message: 'Invalid input!',
                                        }
                                    ]}
                                >
                                    <div>
                                        <label className="labelStyle">DayOfWeek</label>
                                        <Input className="inputStyle" value={this.state.cronSchedule.dayOfWeek.expression}
                                               onChange={(e) => this.handleInputChange('dayOfWeek', e.target.value)}
                                               onFocus={(e) => this.switchFocusFun('dayOfWeek')}
                                        />
                                    </div>
                                </Form.Item>
                            </Col>
                        </Row>
                    </Form>
                    <InputFieldHints focusedField={this.state.focusedField} />
                </div>

            </div>
        );
    }
}


export default App;
