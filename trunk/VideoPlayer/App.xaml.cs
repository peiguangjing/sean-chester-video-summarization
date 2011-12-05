using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Windows;
using System.IO;
namespace VideoPlayer
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        private bool ProcessCommandLine(StartupEventArgs e)
        {
            string videoFileName;
            string audioFileName;
            if (e.Args.Length == 2)
            {
                videoFileName = e.Args[0];
                if (File.Exists(videoFileName))
                {
                    audioFileName = e.Args[1];
                    if (File.Exists(audioFileName))
                    {
                        if (App.Current.MainWindow.IsLoaded)
                        {

                        }
                        else
                        {


                        }
                        return true;
                    }
                    else
                    {
                        MessageBox.Show("Could not find audio file.  Please check that your specified path is correct.  Exiting application.");
                    }
                }
                else
                {
                    MessageBox.Show("Could not find video file.  Please check that your specified path is correct.  Exiting application.");
                }
            }
            else if( e.Args.Length > 2)
            {
                MessageBox.Show("You have specified too many commandline parameters.  Exiting application.");
            }
            return false;
        }

    }
}
