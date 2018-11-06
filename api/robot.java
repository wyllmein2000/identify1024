import  com.alibaba.ocean.tech1024.player.PlayerCommand;
import  com.alibaba.ocean.tech1024.player.PlayerCommandType;
import  com.alibaba.ocean.tech1024.player.PlayerDecider;
import  com.alibaba.ocean.tech1024.player.RobotState;
import  java.io.File;

import  static  com.alibaba.ocean.tech1024.player.PlayerCommandType.*;

/**
  *  @author  xiangfeng.xjw
  */
public  class  PlayerDeciderImpl  implements  PlayerDecider  {



        private  static  final  String  FORWARD_SPEED  =  "25,25";
        private  static  final  String  TURN_SPEED  =  "33,33";
        private  static  final  String  FORWARD_SLEEP_TIME  =  "30";
        private  static  final  String  STOP_SLEEP_TIME  =  "3";


        private  volatile  int  currentState  =  0;
        private  static  final  int  MOVING_SLEEP  =  2;
        private  static  final  int  STOP_SLEEP  =  4;

        private  volatile  int  currentMode  =  MOVE_FORWARD;
        private  static  final  int  BIG_TURN  =  1;
        private  static  final  int  SMALL_TURN  =  2;
        private  static  final  int  MOVE_FORWARD  =  3;

        private  boolean  final_stop  =  false;
        private  boolean  takePhoto  =  true;
        private  boolean  sendMsg  =  true;

        private  PlayerCommandType  prevCommand;
        private  volatile  PlayerCommandType  currentCommand  =  PlayerCommandType.FORWARD;
        private  volatile  String  currentSpeed  =  FORWARD_SPEED;



        private  ForwardSpeed  prevForwardSpeed;
        private  ForwardSpeed  curForwardSpeed;

        //  判断大转向  or  小转向
        private  PlayerCommandType  prevTurnCommand;
        private  TurnSpeed  turnSpeed;
        private  TurnSpeed  curTurnSpeed;
        private  TurnSleepTime  turnSleepTime;

        private  enum  TurnSpeed  {
                HIGH("55,55"),
                LOW("25,25");

                String  speed;
                TurnSpeed(String  speed)  {
                        this.speed  =  speed;
                }
        }

        private  enum  ForwardSpeed  {
                HIGH("55,55"),
                MEDIUM("35,35"),
                LOW("25,25");

                String  speed;
                ForwardSpeed(String  speed)  {
                        this.speed  =  speed;
                }
        }

        private  enum  TurnSleepTime  {
                LONG("300"),
                SHOT("30");

                String  time;
                TurnSleepTime(String  time)  {
                        this.time  =  time;
                }
        }


        @Override
        public  PlayerCommand  decide(RobotState  state)  {
                prevCommand  =  state.getLastCommand();

                if  (final_stop)  {
                        return  cmd(STOP_MOVING);
                }
                if  (prevCommand  ==  TAKE_SHOT)  {
                        final_stop  =  true;
                        return  cmd(SEND_MSG,  "1");
                }
                Integer  frontDistance  =  state.getFrontDistance();
                if  (frontDistance  !=  null  &&  frontDistance  >  0  &&  frontDistance  <  30)  {
                        return  cmd(TAKE_SHOT);
                }
                return  slow2(state);
                //return  fast(state);
        }

        private  void  setTurnSleepTime()  {
                if  (currentCommand  ==  prevTurnCommand)  {
                        turnSleepTime  =  TurnSleepTime.LONG;
                }  else  {
                        turnSleepTime  =  TurnSleepTime.SHOT;
                }
        }

        private  PlayerCommand  slow2(RobotState  state)  {
                if  (currentMode  ==  SMALL_TURN)  {

                        return  smallTurn2(sta te);
        }
        if (state.getLeftIsBlack() == state.getRightIsBlack()) {
            System.out.println("KEEP FORMER ACTION:" + currentCommand);

            return cmd(currentCommand, currentSpeed);
        } else {
            PlayerCommandType preCommandType = currentCommand;
            if (state.getLeftIsBlack()) {
                currentCommand = PlayerCommandType.TURN_LEFT;
            } else {
                currentCommand = PlayerCommandType.TURN_RIGHT;
            }
            currentSpeed = TURN_SPEED;

            if (currentMode == MOVE_FORWARD) {
                return bigTurn2();
            } else if (currentMode == BIG_TURN &&  preCommandType == currentCommand) {
                return bigTurn2();
            } else {
                return smallTurn2(state);
            }
        }
    }

    private PlayerCommand bigTurn2() {
        currentMode = BIG_TURN;
        System.out.println("BIG_BURN:" + currentCommand);
        return cmd(currentCommand, currentSpeed);
    }

    private PlayerCommand smallTurn2(RobotState state) {
        if (currentState == 0) {
            System.out.println("SMALL_TURN 0:" + currentCommand);

            currentMode = SMALL_TURN;
            currentState = MOVING_SLEEP;
            return cmd(currentCommand, currentSpeed);
        } else if (currentState == MOVING_SLEEP) {
            System.out.println("SMALL_TURN MOVING_SLEEP:" + currentCommand);

            currentState = STOP_SLEEP;
            currentCommand = PlayerCommandType.SLEEP;
            return cmd(currentCommand, TurnSleepTime.SHOT.time);
        } else {
            currentState = 0;
            currentMode = MOVE_FORWARD;
            System.out.println("SMALL_TURN STOP_SLEEP:" + currentCommand);


            if (state.getLeftIsBlack() == state.getRightIsBlack()) {
                currentCommand = PlayerCommandType.FORWARD;
                currentSpeed = FORWARD_SPEED;
                return cmd(currentCommand, currentSpeed);
            } else {
                if (state.getLeftIsBlack()) {
                    currentCommand = PlayerCommandType.TURN_LEFT;
                } else {
                    currentCommand = PlayerCommandType.TURN_RIGHT;
                }
                currentSpeed = TURN_SPEED;
                return smallTurn2(state);
            }
        }
    }

    private PlayerCommand slow(RobotState state) {
        // 如果当前是前进，那么sleep保持见前进
        if (isForward(prevCommand)) {
            currentState = MOVING_SLEEP;
            return cmd(SLEEP, FORWARD_SLEEP_TIME);
        }
        // 如果当前是转弯指令，那么sleep保持转弯
        if (isTurn(prevCommand)) {
            currentState = MOVING_SLEEP;
            return cmd(SLEEP, turnSleepTime.time);
        }
        // 如果当前保持运动sleep，则停止
        if (currentState == MOVING_SLEEP) {
            currentState = STOP_SLEEP;
            return cmd(STOP_MOVING);
        }
        // 如果当前是停止，则持续一小段时间
        if (currentState == STOP_SLEEP) {
            currentState = 0;
            return cmd(SLEEP, STOP_SLEEP_TIME);
        }

        String speed;
        if (state.getLeftIsBlack() == state.getRightIsBlack()) {
            currentCommand = PlayerCommandType.FORWARD;
            speed = FORWARD_SPEED;

        } else {
            if (state.getLeftIsBlack()) {
                currentCommand = PlayerCommandType.TURN_LEFT;
            } else {
                currentCommand = PlayerCommandType.TURN_RIGHT;
            }
            setTurnSleepTime();
            speed = TURN_SPEED;
        }
        return cmd(currentCommand, speed);
    }

    private static PlayerCommand cmd(PlayerCommandType type) {
        return PlayerCommand.builder().type(type).build();
    }

    private static PlayerCommand cmd(PlayerCommandType type, String message) {
        return PlayerCommand.builder().type(type).message(message).build();
    }


    private boolean isForward(PlayerCommandType lastCommand) {
        if (lastCommand == null) {
            return false;
        }
        switch (lastCommand) {
            case FORWARD:
            case BACKWARD:
                return true;
        }
        return false;
    }

    private boolean isTurn(PlayerCommandType lastCommand) {
        if (lastCommand == null) {
            return false;
        }
        switch (lastCommand) {
            case TURN_LEFT:
            case TURN_RIGHT:
                return true;
        }
        return false;
    }

}
