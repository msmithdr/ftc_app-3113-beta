package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="NULL TeleOp Testing", group="Teleop")
//@Disabled
public class nullTeleopTesting extends OpMode {
    DcMotor driveRB, driveRF, driveLB, driveLF, spin, shoot;
    double joyRadius, right, left, rightX, leftX, LF_Power, RF_Power, RB_Power,
            LB_Power, LF_Per, LB_Per, RB_Per, RF_Per, rawTotal, timeWait, timeSeq;
    int count;
    Servo hold;
    ElapsedTime elapsed = new ElapsedTime();

    public nullTeleopTesting() {}

    public void init() {
        driveRF = hardwareMap.dcMotor.get("driveRB");
        driveRB = hardwareMap.dcMotor.get("driveRF");
        driveLB = hardwareMap.dcMotor.get("driveLB");
        driveLF = hardwareMap.dcMotor.get("driveLF");
        spin = hardwareMap.dcMotor.get("spin");
        shoot = hardwareMap.dcMotor.get("shoot");
        shoot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hold = hardwareMap.servo.get("hold");
        hold.setPosition(1);
    }

    @Override
    public void start() {
        elapsed.reset();
    }

    @Override
    public void loop() {
        timeWait = elapsed.time() - timeSeq;

        right = gamepad1.left_stick_y;
        left = gamepad1.right_stick_y;
        leftX = gamepad1.right_stick_x;
        rightX = gamepad1.left_stick_x;

        //sets the maximum range of the joystick
        right = Range.clip(right, -1, 1);
        left = Range.clip(left, -1, 1);
        rightX = Range.clip(rightX, -1, 1);
        leftX = Range.clip(leftX, -1, 1);

        RB_Per = Range.clip(RB_Per, -1, 1);
        RF_Per = Range.clip(RF_Per, -1, 1);
        LF_Per = Range.clip(LF_Per, -1, 1);
        LB_Per = Range.clip(LB_Per, -1, 1);

        driveRB.setPower(RB_Per);
        driveLB.setPower(LB_Per);
        driveLF.setPower(LF_Per);
        driveRF.setPower(RF_Per);

        // upon pressing button y, will perform autoshoot command
        if(gamepad2.y) {
            shootingSeq();
        }
        if(!gamepad2.y){
            if(count > 0)
                count = -1;
            if(count == -1) {
                shoot.setPower(0);
                spin.setPower(0);
                hold.setPosition(.5);
                count++;
            }
            timeSeq = 0;
        }
        // kill switch button that stops the shooting and collecting mechanism
        if (gamepad2.x) {
            shoot.setPower(0);
            spin.setPower(0);
            hold.setPosition(1);
        }

        //auto collecting method implemented below
        if(gamepad2.a){
            hold.setPosition(1);
            shoot.setPower(0);
            spin.setPower(.7);
        }

        if (gamepad2.right_bumper) {
            hold.setPosition(.5);
        }
        if (gamepad2.left_bumper) {
            hold.setPosition(1);
        }

        if (gamepad2.right_trigger > .15) {
            shoot.setPower(.35);
        }
        else if (gamepad2.left_trigger > .15) {
            shoot.setPower(0);
        }

        if (gamepad2.dpad_up) {
            spin.setPower(.7);
        }
        else if (gamepad2.dpad_down) {
            spin.setPower(-.7);
        }
        else if (gamepad2.dpad_left) {
            spin.setPower(0);
        }
        else if (gamepad2.dpad_right) {
            spin.setPower(.6);
        }

        //references for joystick values
        RF_Power = (right - rightX + leftX);
        LF_Power = (-right + rightX + leftX);
        LB_Power = (-right - rightX + leftX);

        //Sum of all Joystick values. The small ".000000001" is used to simulate a limit function such that the code never divides by zero.
        RB_Power = Math.abs((right + rightX + leftX) + .000000001);

        //takes each joystick value and divides it by the total to yield a percent that has range of -1<=[motor]_Per <=1
        RB_Per = ((right / RB_Power) + (rightX / RB_Power) + (leftX / RB_Power));
        RF_Per = ((right / RB_Power) - (rightX / RB_Power) + (leftX / RB_Power));
        LB_Per = (-(right / RB_Power) - (rightX / RB_Power) + (leftX / RB_Power));
        LF_Per = (-(right / RB_Power) + (rightX / RB_Power) + (leftX / RB_Power));

        // radius of joystick calculated using pythagorean theorem
        joyRadius = Math.sqrt((right * right) + (rightX * rightX));

        //Threshold established that will allow robot to move slower if the radius of the joystick is less than threshold
        if (joyRadius < .5 && joyRadius > 0) {
            RB_Per = RB_Per / 2;
            RF_Per = RF_Per / 2;
            LF_Per = LF_Per / 2;
            LB_Per = LB_Per / 2;
        }

        telemetry.addData("1", "rightPower", "%5.2f", (right));
        telemetry.addData("2", "leftPower", "%5.2f", (left));
        telemetry.addData("C: mechVar", ":", String.format("%.24f", (joyRadius)));
        telemetry.addData(("4"), ":", String.format("%.24f", (rawTotal)));
        telemetry.update();
    }

    /*
delay method below
measured in milliseconds
 */
    void sleepCool(long sleepTime)    {
        long wakeupTime = System.currentTimeMillis() + sleepTime;
        while (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {}
            sleepTime = wakeupTime - System.currentTimeMillis();
        }
    }

    public void shootingSeq() {
        if (count == 0) {
            timeWait = 0;
            timeSeq = elapsed.time();
            count++;
        }
        if (count == 1) {
            spin.setPower(0);
            hold.setPosition(1);
            shoot.setPower(.45);
            if (timeWait > 1.5) {
                count++;
            }
        }
        if (count == 2) {
            hold.setPosition(.5);
            if (timeWait > 2) {
                count++;
            }
        }
        if (count == 3) {
            spin.setPower(.6);
        }
    }

    @Override
    public void stop() {}
}