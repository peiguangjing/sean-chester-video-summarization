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
using System.ComponentModel;

namespace FinalProject
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window, INotifyPropertyChanged
    {

        private int _videoWidth;
        private int _videoHeight;
        private int _secondsToParse;
        private String _sourceFile;
        private String _outputFile;

        public int VideoWidth 
        {
            get
            {
                return _videoWidth;
            }
            set
            {
                _videoWidth = value;
                NotifyPropertyChanged("VideoWidth");
            }
        }
        public int VideoHeight
        {
            get
            {
                return _videoHeight;
            }
            set
            {
                _videoHeight = value;
                NotifyPropertyChanged("VideoHeight");
            }
        }
        public int SecondsToParse
        {
            get
            {
                return _secondsToParse;
            }
            set
            {
                _secondsToParse = value;
                NotifyPropertyChanged("SecondsToParse");
            }
        }
        public String SourceFile
        {
            get
            {
                return _sourceFile;
            }
            set
            {
                _sourceFile = value;
                NotifyPropertyChanged("SourceFile");
            }
        }
        public String OutputFile
        {
            get
            {
                return _outputFile;
            }
            set
            {
                _outputFile = value;
                NotifyPropertyChanged("OutputFile");
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        private void NotifyPropertyChanged(String info)
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(info));
            }
        }

        public MainWindow()
        {
            InitializeComponent();
            VideoHeight = 480;
            VideoWidth = 640;
            
        }

        private void OnOpenSourceClick(object sender, RoutedEventArgs e)
        {
            Microsoft.Win32.OpenFileDialog dialog = new Microsoft.Win32.OpenFileDialog();
            if (dialog.ShowDialog() == true)
            {
                SourceFile = dialog.FileName;
            }
        }

        private void OnPlaySourceClick(object sender, RoutedEventArgs e)
        {
            mediaElement1.Play();
        }

        private void OnConvertToRGBClick(object sender, RoutedEventArgs e)
        {
            mediaElement1.Pause();
            int [] buffer = new int[(int)(mediaElement1.RenderSize.Height * mediaElement1.RenderSize.Width * 4)];
            RenderTargetBitmap renderTarget = new RenderTargetBitmap((int)mediaElement1.RenderSize.Width,
             (int)mediaElement1.RenderSize.Height, 96, 96, PixelFormats.Pbgra32);
            VisualBrush sourceBrush = new VisualBrush(mediaElement1);

            DrawingVisual drawingVisual = new DrawingVisual();
            DrawingContext drawingContext = drawingVisual.RenderOpen();

            using (drawingContext)
            {
                drawingContext.DrawRectangle(sourceBrush, null, new Rect(new Point(0, 0),
                    new Point(mediaElement1.RenderSize.Width, mediaElement1.RenderSize.Height)));
            }
            renderTarget.Render(drawingVisual);

            renderTarget.CopyPixels(buffer, renderTarget.PixelWidth * ((PixelFormats.Pbgra32.BitsPerPixel + 7) / 8), 0);

            using (System.IO.StreamWriter file = new System.IO.StreamWriter(@"rgbOutput.rgb", true))
            {
                foreach (int value in buffer)
                {
                    file.Write("{0}, ", value.ToString());
                }
            } 

        }

        public void OtherConvert()
        {
            
        }
    }
}
