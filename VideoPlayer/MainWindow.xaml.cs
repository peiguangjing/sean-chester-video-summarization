using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using Microsoft.Win32;
using System.ComponentModel;
using System.Timers;

namespace VideoPlayer
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window, INotifyPropertyChanged
    {
        private Timer timer = new Timer();
        //public VideoViewModel Video { get; set; }

        public event PropertyChangedEventHandler PropertyChanged;

        protected void NotifyPropertyChanged(String info)
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(info));
            }
        }

        public MainWindow()
        {
            InitializeComponent();
            //timer.Elapsed += (obj, args) => { Dispatcher.Invoke((Action)delegate() { Video.OnVideoTimerTick(); },System.Windows.Threading.DispatcherPriority.Send); };
            //timer.Interval = 1000.0f / 24.0f;
            //Video = new VideoViewModel();
        }

        private void FileOpen_Click(object sender, RoutedEventArgs e)
        {
            String videoFile;

            OpenFileDialog fileDialog = new OpenFileDialog();

            fileDialog.Title = "Open Video File";
            if (fileDialog.ShowDialog() == true)
            {
                videoFile = fileDialog.FileName;
                fileDialog = new OpenFileDialog();
                fileDialog.Title = "Open Audio File";
                if (fileDialog.ShowDialog() == true)
                {
                    if (!video.OpenFile(videoFile,fileDialog.FileName))
                    {
                        Console.WriteLine("Error opening/parsing video");
                    }
                }
            }
        }

        private void Play_Click(object sender, RoutedEventArgs e)
        {
            video.Play();
            //timer.Start();
        }

        private void Pause_Click(object sender, RoutedEventArgs e)
        {
            video.Pause();
        }

        private void Stop_Click(object sender, RoutedEventArgs e)
        {
            video.Stop();
        }
    }
}
