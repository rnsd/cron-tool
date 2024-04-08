import React, {Component} from 'react';
import {Table} from 'antd';
import './InputFieldHints.module.css';

const genericHintContent = [
    {
        key: '1',
        col1: '*',
        col2: 'Wildcard - any value',
    },
    {
        key: '2',
        col1: ',',
        col2: 'List definition',
    },
    {
        key: '3',
        col1: '-',
        col2: 'Range definition',
    },
    {
        key: '4',
        col1: '/',
        col2: 'Step definition',
    }
];

interface InputHintsProps {
    focusedField: string;
}

class InputHints extends Component<InputHintsProps> {
    render() {
        let variableHintContent: { key: string; col1: string; col2: string; }[];

        switch (this.props.focusedField) {
            case 'minute':
                variableHintContent = [{
                    key: '5',
                    col1: '0-59',
                    col2: 'Valid values',
                }];
                break;
            case 'hour':
                variableHintContent = [{
                    key: '5',
                    col1: '0-23',
                    col2: 'Valid values',
                }];
                break;
            case 'dayOfMonth':
                variableHintContent = [{
                    key: '5',
                    col1: '1-31',
                    col2: 'Valid values',
                }];
                break;
            case 'month':
                variableHintContent = [{
                    key: '5',
                    col1: '1-12',
                    col2: 'Valid numerical values',
                },{
                    key: '6',
                    col1: 'JAN-DEC',
                    col2: 'Valid descriptive values',
                }];
                break;
            case 'dayOfWeek':
                variableHintContent = [{
                    key: '5',
                    col1: '0-6',
                    col2: 'Valid numerical values',
                },{
                    key: '6',
                    col1: 'SUN-SAT',
                    col2: 'Valid descriptive values',
                }];
                break;

            // continue for other cases...
            default:
                variableHintContent = [];
        }

        const columns = [
            {
                title: 'First Column',
                dataIndex: 'col1',
                key: 'col1',
                width: '10  0px',
                align: 'right' as const,
                render: (text: string) => <span style={{fontWeight: 'bold'}}>{text}</span>,
            },
            {
                title: 'Second Column',
                dataIndex: 'col2',
                key: 'col2',
                width: '250px'
            }
        ];

        const data = [...genericHintContent, ...variableHintContent];

        return (
            <Table className="my-table" columns={columns} dataSource={data} pagination={false} showHeader={false}/>
        );
    }
}

export default InputHints;