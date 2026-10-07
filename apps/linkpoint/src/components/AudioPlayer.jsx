import { useState, useRef, useEffect } from "react";

export function AudioPlayer({ src, onError, className = "", style }) {
  const audioRef = useRef(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [volume, setVolume] = useState(1.0);
  const [isMuted, setIsMuted] = useState(false);

  useEffect(() => {
    if (audioRef.current) {
      audioRef.current.volume = isMuted ? 0 : volume;
      audioRef.current.muted = isMuted;
    }
  }, [volume, isMuted, src]);

  const togglePlay = () => {
    if (!audioRef.current) return;
    if (isPlaying) {
      audioRef.current.pause();
      setIsPlaying(false);
    } else {
      const promise = audioRef.current.play();
      if (promise !== undefined && typeof promise.then === "function") {
        promise
          .then(() => {
            setIsPlaying(true);
          })
          .catch((err) => {
            setIsPlaying(false);
            if (onError) onError(err);
          });
      } else {
        setIsPlaying(true);
      }
    }
  };

  const handleVolumeChange = (e) => {
    const newVol = parseFloat(e.target.value);
    setVolume(newVol);
    if (newVol > 0 && isMuted) {
      setIsMuted(false);
    }
    if (audioRef.current) {
      audioRef.current.volume = newVol;
    }
  };

  const toggleMute = () => {
    const nextMuted = !isMuted;
    setIsMuted(nextMuted);
    if (audioRef.current) {
      audioRef.current.muted = nextMuted;
    }
  };

  const handleError = (e) => {
    setIsPlaying(false);
    if (onError) onError(e);
  };

  return (
    <div
      className={`audio-player ${className}`.trim()}
      style={{ display: "flex", alignItems: "center", gap: "8px", ...style }}
    >
      <audio
        ref={audioRef}
        src={src}
        autoPlay={false}
        onError={handleError}
        onPlay={() => setIsPlaying(true)}
        onPause={() => setIsPlaying(false)}
        onEnded={() => setIsPlaying(false)}
      />
      <button
        type="button"
        onClick={togglePlay}
        aria-label={isPlaying ? "Pause audio stream" : "Play audio stream"}
      >
        {isPlaying ? "Pause" : "Play"}
      </button>
      <input
        type="range"
        min="0"
        max="1"
        step="0.05"
        value={isMuted ? 0 : volume}
        onChange={handleVolumeChange}
        aria-label="Volume"
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={Math.round((isMuted ? 0 : volume) * 100)}
      />
      <button
        type="button"
        onClick={toggleMute}
        aria-label={isMuted ? "Unmute audio stream" : "Mute audio stream"}
        aria-pressed={isMuted}
      >
        {isMuted ? "Unmute" : "Mute"}
      </button>
    </div>
  );
}

export default AudioPlayer;
