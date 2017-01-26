package ui;

import client.TaskStopOtherActivity;
import common.msg.MessageConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.Timer;

/**
 * This class demonstrates the use of Timer class
 */
public class TimerDemo extends JToolBar implements
ActionListener,MessageConstants
{
   
    public Timer m_stopWatchTimer;
    public int m_duration = 0;
    public int m_stopWatchTick;
    public JLabel m_watchLabel;
    public Calendar m_stopWatch;
    public JLabel taskLabel = null;
    public int event_break_id = 0;

    /**
     * Constructor
     */
    public TimerDemo()
    {
        m_duration = 0;
        createDialogComponents();
        m_stopWatchTimer = new Timer(1000, this);
        m_stopWatchTimer.start();
        setVisible(true);
    }
    
   

    /**
     * Creates the dialogs component
     */
    private void createDialogComponents()
    {
        JPanel displayPanel = new JPanel(new BorderLayout());
        m_watchLabel = new JLabel();
        m_watchLabel.setHorizontalAlignment(JLabel.CENTER);
        m_watchLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Date d = new Date();
        d.setHours(0);
        d.setMinutes(0);
        d.setSeconds(0);

        taskLabel = new JLabel();
        taskLabel.setHorizontalAlignment(JLabel.CENTER);
        taskLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        m_stopWatch = Calendar.getInstance();
        m_stopWatch.setTime(d);
        m_stopWatch.set(Calendar.SECOND, m_duration);   
        //m_stopWatch.set(Calendar.HOUR_OF_DAY,24);
        
        updateStopWatch();       //Call to update the timer.
              
        displayPanel.add(BorderLayout.NORTH, m_watchLabel);
        displayPanel.add(BorderLayout.CENTER, taskLabel);
        displayPanel.setAlignmentY(0);
        m_watchLabel.setFont(new Font("Serif", Font.BOLD, 12));
        taskLabel.setFont(new Font("Serif", Font.BOLD, 12));
        this.add( displayPanel,BorderLayout.NORTH);
    }

   //Updates the timer.
    private void updateStopWatch()    
    {  
        String time = getDateInStringFormat(m_stopWatch);              
        m_watchLabel.setText("Time : " + time);
        
        String[] taskArray = taskLabel.getText().trim().split(":");
        if(taskArray.length == 2){
           if(taskArray[1].trim().equalsIgnoreCase("Break") || taskArray[1].trim().equalsIgnoreCase("Others")){
               pingTheServer();
           }
         }
    }
    
    //Gets the required Date format.
    private String getDateInStringFormat( java.util.Calendar cal)            
    {
        String hh = getVal((cal.getTime()).getHours());
        String mm = getVal((cal.getTime()).getMinutes());
        String ss = getVal((cal.getTime()).getSeconds());                
        String time = hh + " : " + mm  + " : " + ss;       
        return time;
    }
    
    //Gets the formated value.
    private String getVal(int tm)
    {
        String formatedValue = "" + tm;
        if(tm<10)
        {
            formatedValue = "0" + tm;
        }
      
        return formatedValue;
    }
    
    /**
     * This method is implementation for the ActionListner interface
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {

        if(e.getSource().equals(m_stopWatchTimer))
        {
            m_stopWatchTick++;
           
            if(m_stopWatchTick == m_duration)
            {
				
                destroy();        
            }
            else
            {
                m_stopWatch.set(Calendar.MILLISECOND, 1000);
                updateStopWatch();
            }                
        }
      
    }        
    
    /**
     * destroys the demo by stopping the timers
     */
    public String destroy()
    {
	System.out.println("Elapsed time----->>"+m_stopWatch.getTime());         
        m_stopWatchTimer.stop();
        //taskLabel.setText(" ");
        String timerValue = m_stopWatch.getTime().getHours() + ":" + m_stopWatch.getTime().getMinutes() + ":" + m_stopWatch.getTime().getSeconds();
        return timerValue;
    }
    
    //Restarts the timer from last duration.
    public void resume(String duration){
       Timer stopWatchTimer = new Timer(1000, this);
        stopWatchTimer.start();
        String[] getTimeInHHMMSS = duration.split(":");
        Date d = new Date();
        d.setHours(Integer.parseInt(getTimeInHHMMSS[0]));
        d.setMinutes(Integer.parseInt(getTimeInHHMMSS[1]));
        d.setSeconds(Integer.parseInt(getTimeInHHMMSS[2]));
        m_stopWatch.setTime(d);
        updateStopWatch();
    }
   
    
     //Pings the server in every 2 min to prevent the session time out.
   public void pingTheServer(){
      int getTimeInMin = m_stopWatch.getTime().getMinutes();
      int getTimeInSec = m_stopWatch.getTime().getSeconds();
      if(getTimeInMin > 0 && (getTimeInMin % 2 == 0 && getTimeInSec == 0)){
         Calendar timeStamp = Calendar.getInstance();
         long close_timestamp = timeStamp.getTimeInMillis();
         TaskStopOtherActivity task = new TaskStopOtherActivity(event_break_id,close_timestamp);
         task.enqueue(this);        
      }
   }
}
