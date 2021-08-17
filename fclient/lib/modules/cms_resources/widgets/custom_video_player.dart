import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:chewie/chewie.dart';
import 'package:video_player/video_player.dart';

class CustomVideoPlayer extends StatefulWidget {
  final String title;
  final String videoURI;

  const CustomVideoPlayer(
      {Key? key, required this.title, required this.videoURI})
      : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _CustomVideoPlayerState();
  }
}

class _CustomVideoPlayerState extends State<CustomVideoPlayer> {
  late VideoPlayerController _videoPlayerController;
  ChewieController? _chewieController;

  @override
  void initState() {
    super.initState();
    initializePlayer();
  }

  @override
  void dispose() {
    _videoPlayerController.dispose();
    _chewieController?.dispose();
    super.dispose();
  }

  Future<void> initializePlayer() async {
    _videoPlayerController = VideoPlayerController.network(widget.videoURI);
    await Future.wait([_videoPlayerController.initialize()]);
    _createChewieController();
    setState(() {});
  }

  void _createChewieController() {
    _chewieController = ChewieController(
      videoPlayerController: _videoPlayerController,
      allowFullScreen: true,
      autoPlay: true,
      looping: true,
    );
  }

  @override
  Widget build(BuildContext context) {
    return _chewieController == null
        ? CircularProgressIndicator()
        : Padding(
            padding: const EdgeInsets.all(8.0),
            child: Chewie(
              controller: _chewieController as ChewieController,
            ),
          );
  }
}
