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
using System.Drawing;
using System.Runtime.InteropServices;
using System.Windows.Interop;
using System.Windows.Threading;
using System.Timers;
using System.ComponentModel;

using Color = System.Drawing.Color;
using Image = System.Windows.Controls.Image;
using System.Diagnostics;

namespace VideoPlayer
{

    /// <summary>
    /// Interaction logic for Video.xaml
    /// </summary>
    public partial class Video : Image, INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;

        protected void NotifyPropertyChanged(String info)
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(info));
            }
        }

        public VideoViewModel VideoModel { get; set; }

        private static float MillisecondsPerFrame = 1000.0f / 24.67f;

        private int _frameCounter;
        public int FrameCounter 
        {
            get
            {
                return _frameCounter;
            }
            private set
            {
                _frameCounter = value;
                NotifyPropertyChanged("FrameCounter");
            }
        }

        private Stopwatch _stopwatch;
        private float RunningTime { get; set; }

        private WriteableBitmap BitmapSource = new WriteableBitmap(320, 240, 96, 96, System.Windows.Media.PixelFormats.Rgb24, null);

        public bool OpenFile(String videoFileName, String audioFileName)
        {
            return VideoModel.OpenFile(videoFileName, audioFileName);
        }

        public void Play(bool StartTimer = false)
        {
            Source = BitmapSource;
            VideoModel.Play();
            _stopwatch = new Stopwatch();
            _stopwatch.Start();
            RunningTime = 0.0f;
        }

        public void Pause()
        {
            VideoModel.Pause();
        }

        public void Stop()
        {
            VideoModel.Stop();
        }

        public Video()
        {
            InitializeComponent();

            CompositionTarget.Rendering += new EventHandler(CompositionTarget_Rendering);

            VideoModel = new VideoViewModel();
        }

        void CompositionTarget_Rendering(object sender, EventArgs e)
        {
            if (VideoModel.IsPlaying())
            {
                RunningTime += _stopwatch.Elapsed.Milliseconds;
                //RunningTime += _stopwatch.ElapsedTicks * 1000.0f / Stopwatch.Frequency;
                //if (MillisecondsPerFrame * FrameCounter <= RunningTime)
                if( MillisecondsPerFrame <= RunningTime)
                {
                    //Console.WriteLine("{0}", RunningTime);
                    RunningTime -= MillisecondsPerFrame;
                    _stopwatch.Restart();
                    FrameCounter++;
                    VideoModel.OnVideoTimerTick(BitmapSource);
                }
                else
                {
                    _stopwatch.Restart();
                }
            }
        }
    }
}
