import { LinearGradient } from "expo-linear-gradient";
import { useEffect, useRef, useState } from "react";
import {
  ActivityIndicator,
  Animated,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import Svg, { Circle } from "react-native-svg";

export default function App() {
  // ----- Hooks -----
  const [data, setData] = useState(null);   // fetched data
  const [selected, setSelected] = useState(null);
  const [loading, setLoading] = useState(false);
  const toastAnim = useRef(new Animated.Value(0)).current;
  const gaugeAnim = useRef(new Animated.Value(0)).current;

  // ----- Fetch data -----
  const fetchData = async () => {
    try {
      setLoading(true);
      const res = await fetch("https://migraine-junction.onrender.com/api/v1/latest");

      const contentType = res.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        const json = await res.json();
        setData(json);
      } else {
        const text = await res.text(); 
        console.warn("Expected JSON but got:", text);
        setData(null);
      }

      setSelected(null);
      setLoading(false);
    } catch (err) {
      console.error("Fetch error:", err);
      setData(null);
      setLoading(false);
    }
  };

  // Fetch once on mount
  useEffect(() => {
    fetchData();
  }, []);

  // ----- Derived values -----
  const current = data && data.top_factors?.length
    ? {
        p: data.p_next_hour,
        f: data.top_factors.map((f) => {
          let d = f.score > 0.35 ? "up" : "down";
          return { k: f.feature.split("_")[0], i: f.score, d };
        }),
      }
    : { p: 0, f: [] };

  const pct = Math.round((data?.p_next_hour || 0) * 100);
  // const pct = Math.round(current.p * 100);
  // const ringColor = current.p >= 0.7 ? "#f87171" : current.p >= 0.3 ? "#fbbf24" : "#34d399";
  // const label = current.p >= 0.7 ? "High" : current.p >= 0.3 ? "Elevated" : "Low";

  const riskLevel =
    pct >= 70 ? "High" :
    pct >= 30 ? "Elevated" :
    "Low";


  const ringColor = riskLevel === "High" ? "#f87171" : riskLevel === "Elevated" ? "#fbbf24" : "#34d399";


  // // ----- Gauge animation -----
  // useEffect(() => {
  //   Animated.timing(gaugeAnim, { toValue: pct, duration: 600, useNativeDriver: false }).start();
  // }, [pct]);

  // ----- Toast -----
  const showToast = () => {
    Animated.sequence([
      Animated.timing(toastAnim, { toValue: 1, duration: 200, useNativeDriver: true }),
      Animated.delay(900),
      Animated.timing(toastAnim, { toValue: 0, duration: 250, useNativeDriver: true }),
    ]).start();
  };

  // ----- Helpers -----
  const featureName = (f) => {
    if (f.includes("stress")) return "Stress";
    if (f.includes("workload")) return "Workload";
    if (f.includes("hrv")) return "HRV";
    return f;
  };

  const renderChips = () => {
    if (!data || !data.top_factors) return null;

    return data.top_factors.map((f, i) => {
      const key = f.feature;
      const imp = Math.round(f.score * 100);
      const tone = f.score > 0.35 ? "#f87171" : f.score > 0.2 ? "#fbbf24" : "#34d399";
      const emoji = f.feature.includes("hrv") ? "💓" : f.feature.includes("stress") ? "😮‍💨" : "📅";
      const arrow = f.score >= 0.5 ? "↑" : "↓";
      const active = selected === key;
      return (
        <TouchableOpacity
          key={i}
          onPress={() => setSelected(selected === key ? null : key)}
          style={[
            styles.chip,
            { borderColor: active ? "#67e8f9" : "#2a2a2a", backgroundColor: active ? "rgba(103,232,249,0.09)" : "#111" },
          ]}
        >
          <Text style={styles.chipText}>
            {emoji} <Text style={{ fontWeight: "600" }}>{featureName(f.feature)}</Text>{" "}
            <Text style={{ color: tone }}>{arrow} {imp}%</Text>
          </Text>
        </TouchableOpacity>
      );
    });
  };

  const renderActions = () => {
    if (!data || !data.recommended_actions) return null;

    const acts = selected ? data.recommended_actions : [];
    if (!acts.length) return <Text style={{ color: "#888", fontSize: 13 }}>Select a driver to see actions</Text>;
    return acts.map((a, i) => (
      <TouchableOpacity key={i} style={styles.actionPill} onPress={showToast}>
        <Text style={styles.actionText}>{a}</Text>
      </TouchableOpacity>
    ));
  };

  // ----- Gauge SVG -----
  const size = 180;
  const strokeWidth = 12;
  const radius = (size - strokeWidth) / 2;
  // const circumference = 2 * Math.PI * radius;

  return (
    <ScrollView style={styles.container} contentContainerStyle={{ paddingBottom: 60, paddingTop: 40 }}>
      <View style={styles.header}>
        <View style={styles.headerLeft}>
          <LinearGradient colors={["#22d3ee", "#8b5cf6"]} style={styles.logo}>
            <Text style={styles.logoText}>μ</Text>
          </LinearGradient>
          <View>
            <Text style={styles.title}>Today Migraine Risk</Text>
            <Text style={styles.updated}>Updated —</Text>
          </View>
        </View>
      </View>

      <View style={styles.gaugeCard}>
        {loading ? (
          <ActivityIndicator size="large" color={ringColor} />
        ) : (
          <View style={styles.gaugeWrapper}>
            <Svg width={size} height={size} style={{ transform: [{ rotate: "-90deg" }] }}>
              <Circle
                cx={size / 2}
                cy={size / 2}
                r={radius}
                stroke="#111"
                strokeWidth={strokeWidth}
                fill="none"
              />
              <Circle
                cx={size / 2}
                cy={size / 2}
                r={radius}
                stroke={ringColor}
                strokeWidth={strokeWidth}
                strokeLinecap="round"
                fill="none"
              />
            </Svg>
            <View style={styles.gaugeInner}>
              <Text style={styles.gaugePct}>{pct}%</Text>
              <Text style={styles.gaugeBand}>{riskLevel} risk next hour</Text>
            </View>
          </View>
        )}
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Top drivers</Text>
        <View style={styles.chipContainer}>{renderChips()}</View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Recommended actions</Text>
        <View style={styles.actionsContainer}>{renderActions()}</View>
      </View>

      <Animated.View
        pointerEvents="none"
        style={[
          styles.toast,
          {
            opacity: toastAnim,
            transform: [{ translateY: toastAnim.interpolate({ inputRange: [0, 1], outputRange: [40, 0] }) }],
          },
        ]}
      >
        <Text style={styles.toastText}>Action logged</Text>
      </Animated.View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#080808" },
  header: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", padding: 16, paddingTop: 50 },
  headerLeft: { flexDirection: "row", alignItems: "center" },
  logo: { width: 42, height: 42, borderRadius: 10, alignItems: "center", justifyContent: "center" },
  logoText: { fontWeight: "700", color: "#000" },
  title: { fontWeight: "600", color: "#fff", fontSize: 16 },
  updated: { color: "#9ca3af", fontSize: 12 },
  gaugeCard: { marginHorizontal: 16, marginTop: 6, marginBottom: 18, alignItems: "center" },
  gaugeWrapper: { width: 200, height: 200, alignItems: "center", justifyContent: "center" },
  gaugeInner: { position: "absolute", alignItems: "center", justifyContent: "center" },
  gaugePct: { fontSize: 36, fontWeight: "800", color: "#fff" },
  gaugeBand: { color: "#9ca3af", fontSize: 13, marginTop: 6 },
  section: { paddingHorizontal: 16, marginBottom: 18 },
  sectionTitle: { color: "#9ca3af", fontSize: 12, marginBottom: 8, textTransform: "uppercase" },
  chipContainer: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
  chip: { paddingVertical: 8, paddingHorizontal: 12, borderRadius: 999, borderWidth: 1, marginRight: 8, marginBottom: 8 },
  chipText: { color: "#fff", fontSize: 14 },
  actionsContainer: { marginTop: 8 },
  actionPill: { backgroundColor: "#0f0f0f", borderRadius: 12, padding: 10, marginBottom: 8, borderWidth: 1, borderColor: "#202020" },
  actionText: { color: "#e6e6e6" },
  toast: { position: "absolute", left: 0, right: 0, bottom: 28, alignItems: "center" },
  toastText: { backgroundColor: "#0b0b0b", color: "#fff", paddingHorizontal: 16, paddingVertical: 10, borderRadius: 12, borderWidth: 1, borderColor: "#222" },
});