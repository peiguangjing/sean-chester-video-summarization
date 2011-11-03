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

namespace VideoPlayer
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window, INotifyPropertyChanged
    {
        public Video video { get; set; }

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
            video = new Video();
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
        }
    }
}
