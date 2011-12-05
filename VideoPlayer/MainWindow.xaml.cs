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
        private bool QueuedPlay { get; set; }
        private string QueuedVideoFile { get; set; }
        private string QueuedAudioFile { get; set; }
        public Video Video { get { return video; } }

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
            
        }

        public void OpenAndPlay()
        {
            if (!video.OpenFile(QueuedVideoFile, QueuedAudioFile))
            {
                Console.WriteLine("Error opening/parsing video");

            }
            else
            {
                System.Threading.Thread.Sleep(200);
                video.Play();
            }
        }

        public void OpenAndPlay(string videoFile, string audioFile)
        {
            if (!video.OpenFile(videoFile, audioFile))
            {
                Console.WriteLine("Error opening/parsing video");
                System.Threading.Thread.Sleep(200);
                video.Play();
            }
        }

        public void QueuedOpenAndPlay(string videoFile, string audioFile)
        {
            QueuedPlay = true;
            QueuedVideoFile = videoFile;
            QueuedAudioFile = audioFile;
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

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            if (QueuedPlay)
            {
                OpenAndPlay();
            }
        }
    }
}
