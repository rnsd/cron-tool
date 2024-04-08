import React, { ChangeEvent, FunctionComponent } from "react";
import { Form, Input } from 'antd';

interface CronFieldProps {
    name: string;
    initialValue: string;
    onInputChange: (value: string) => void;
}

const CronField: FunctionComponent<CronFieldProps> = (props: CronFieldProps) => {
    const { name, initialValue = '*', onInputChange } = props;

    const validate = (value: string): string => value.match(/^[\d*,\/,-]+$/) ? '' : 'Invalid input!';

    const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
        const value: string = e.target.value;
        const errorMessage = validate(value);
        if (!errorMessage) {
            onInputChange(value);
        }
    };

    return (
        <Form.Item
            className="inputContainer"
            name={name}
        >
            <div>
                <label className="labelStyle">{name}</label>
                <Input
                    className="inputStyle"
                    defaultValue={initialValue}
                    onChange={handleInputChange}
                />
            </div>
        </Form.Item>
    );
}

export default CronField;