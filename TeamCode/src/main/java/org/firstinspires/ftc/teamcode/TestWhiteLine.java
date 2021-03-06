package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

import ftc.vision.BeaconColorResult;
import ftc.vision.FrameGrabber;
import ftc.vision.ImageProcessorResult;

/**
 * Created by Mac on 12/19/2016.
 */
@Autonomous(name="Test White Line", group="Test")
//@Disabled
public class TestWhiteLine extends OpMode{
    FrameGrabber frameGrabber = FtcRobotControllerActivity.frameGrabber; //Get the frameGrabber
    DcMotor motorRB, motorRF, motorLB, motorLF, spin, shoot;
    double timeAuto = 0, timeStart = 0;
    Servo hold;
    byte[] colorCcache;
    I2cDevice colorC;
    I2cDeviceSynch colorCreader;
    BeaconColorResult result;
    boolean bool = false;

    public TestWhiteLine()  {}

    public void init() {
        colorC = hardwareMap.i2cDevice.get("line");
        colorCreader = new I2cDeviceSynchImpl(colorC, I2cAddr.create8bit(0x3c), false);
        colorCreader.engage();
        colorCreader.write8(3, 0);
    }

    @Override
    public void start() {
        // defines timeStart as the timer at the start of autonomous to preserve an initial value
        timeAuto = 0;
        timeStart = this.time;
    }

    @Override
    public void loop() {
        timeAuto = this.time - timeStart;

        colorCcache = colorCreader.read(0x04, 1);

        if(colorCcache[0] > 6) {
            bool = true;
        } else  {
            bool = false;
        }

        telemetry.addData("Result", result);
        telemetry.addData("#C", colorCcache[0] & 0xFF);
        telemetry.addData("White people be like", bool);
        telemetry.update();
        //wait before quitting (quitting clears telemetry)
        sleepCool(1000);
    }

    //delay method below
    public static void sleepCool(long sleepTime)    {
        long wakeupTime = System.currentTimeMillis() + sleepTime;
        while (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e) {}
            sleepTime = wakeupTime - System.currentTimeMillis();
        }
    } //sleep

    @Override
    public void stop() {}
}
