// Copyright (c) 2023 FRC Team 2881 - The Lady Cans
//
// Open Source Software; you can modify and/or share it under the terms of BSD
// license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.auto.FollowTrajectory;
import frc.robot.commands.drive.DriveWithJoysticks;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Suction;

public class RobotContainer {
  private final XboxController m_driverController = new XboxController(0);
  private final XboxController m_manipulatorController = new XboxController(1);
  private final PathPlannerTrajectory simplePath = PathPlanner.loadPath("SimplePath", 1, 1);
  Drive m_drive = new Drive();
  Suction m_suction = new Suction();

  private final DriveWithJoysticks m_driveWithJoysticks = new DriveWithJoysticks(
      m_drive,
      () -> applyDeadband(-m_driverController.getLeftY()),
      () -> applyDeadband(-m_driverController.getLeftX()),
      () -> applyDeadband(-m_driverController.getRightX()));

  public RobotContainer() {
    configureControllers();
    m_drive.setDefaultCommand(m_driveWithJoysticks);
  }

  public double applyDeadband(double input) {
    if (Math.abs(input) < 0.1) {
      return 0.0;
    } else {
      return input;
    }
  }

  private void configureControllers() {

    //DRIVER
    new Trigger(m_driverController::getBackButton)
      .whileTrue(new RunCommand(m_drive::zeroHeading, m_drive));

    //MANIPULATOR
    new Trigger(m_manipulatorController::getAButtonPressed)
      .onTrue(new InstantCommand(() -> { m_suction.enable(); }));

    new Trigger(m_manipulatorController::getBButtonPressed)
      .onTrue(new InstantCommand(() -> { m_suction.disable(); }));

  }

  public Command getAutonomousCommand() {
    return new FollowTrajectory(simplePath, true, m_drive);
  }
}
