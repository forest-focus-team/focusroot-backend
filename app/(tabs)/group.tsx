import { Client } from "@stomp/stompjs";
import React, { useEffect, useRef, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Button,
  FlatList,
  StyleSheet,
  Text,
  View,
} from "react-native";
import "text-encoding"; // Đảm bảo polyfill text-encoding chạy mượt mà
import { apiClient } from "../../constants/axiosClient";

// Định nghĩa kiểu dữ liệu thành viên trong nhóm tập trung
interface GroupMember {
  userId: string;
  username: string;
  status: "FOCUSING" | "IDLE"; // Đang tập trung hoặc đang treo máy
}

export default function GroupScreen() {
  const [members, setMembers] = useState<GroupMember[]>([]);
  const [connected, setConnected] = useState(false);
  const [joining, setJoining] = useState(false);
  const [inGroup, setInGroup] = useState(false);

  const stompClientRef = useRef<Client | null>(null);
  const currentGroupId = "focusroot-global-room"; // Ví dụ ID phòng chung của team

  // 1. Hàm kết nối WebSocket qua STOMP (Yêu cầu số 4)
  const connectWebSocket = (groupId: string) => {
    // Điệp lưu ý: Thay http:// bằng ws:// từ địa chỉ IP backend của bạn nhé
    const socketUrl = "ws://192.168.1.10:5000/ws";

    const client = new Client({
      brokerURL: socketUrl,
      reconnectDelay: 5000, // Tự động kết nối lại sau 5s nếu mất mạng
      debug: (str) => console.log("STOMP Debug: ", str),
    });

    client.onConnect = (frame) => {
      setConnected(true);
      setJoining(false);
      setInGroup(true);

      // Đăng ký (Subscribe) nhận dữ liệu thay đổi trạng thái phòng từ Backend
      client.subscribe(`/topic/group/${groupId}`, (message) => {
        if (message.body) {
          const updatedMembers = JSON.parse(message.body);
          setMembers(updatedMembers); // Cập nhật danh sách phòng theo thời gian thực
        }
      });

      // Gửi tin nhắn thông báo mình vừa Join phòng (Publish)
      client.publish({
        destination: `/app/group/${groupId}/join`,
        body: JSON.stringify({ userId: "user-123", username: "Điệp Ngô" }), // Giả lập dữ liệu user của bạn
      });
    };

    client.onStompError = (frame) => {
      Alert.alert(
        "Lỗi kết nối",
        "Không thể kết nối vào hệ thống phòng học Realtime!",
      );
      setJoining(false);
    };

    client.activate(); // Bắt đầu kích hoạt kết nối
    stompClientRef.current = client;
  };

  // 2. Hàm xử lý khi bấm nút Tham Gia Nhóm
  const handleJoinGroup = async () => {
    setJoining(true);
    try {
      // Gọi API Rest thông thường để kiểm tra tính hợp lệ hoặc ghi danh trước
      await apiClient.post(`/api/groups/${currentGroupId}/join`);

      // Nếu API trả về ổn, tiến hành mở kết nối Socket Realtime
      connectWebSocket(currentGroupId);
    } catch (error: any) {
      // Nếu chưa có API thật hoặc lỗi, ta fallback kết nối thẳng Socket để test UI luôn
      console.log("Fallback to direct socket connection");
      connectWebSocket(currentGroupId);
    }
  };

  // Hủy kết nối khi user thoát màn hình này
  useEffect(() => {
    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
      }
    };
  }, []);

  const renderMemberItem = ({ item }: { item: GroupMember }) => (
    <View style={styles.memberCard}>
      <Text style={styles.avatar}>👤</Text>
      <View style={{ flex: 1 }}>
        <Text style={styles.memberName}>{item.username}</Text>
        <Text
          style={[
            styles.statusTag,
            item.status === "FOCUSING" ? styles.focusTag : styles.idleTag,
          ]}
        >
          {item.status === "FOCUSING"
            ? "⏳ Đang tập trung"
            : "💤 Đang nghỉ ngơi"}
        </Text>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.title}>👥 Phòng Tập Trung Nhóm</Text>
      <Text style={styles.subtitle}>
        Đồng hành tập trung theo thời gian thực cùng đồng đội FocusRoot
      </Text>

      {!inGroup ? (
        <View style={styles.joinBox}>
          <Text style={styles.infoText}>
            Bạn chưa tham gia vào phòng học chung của nhóm.
          </Text>
          {joining ? (
            <ActivityIndicator size="large" color="#007AFF" />
          ) : (
            <Button
              title="🚀 Tham Gia Phòng Học Ngay"
              color="#007AFF"
              onPress={handleJoinGroup}
            />
          )}
        </View>
      ) : (
        <View style={{ flex: 1 }}>
          <View style={styles.statusBanner}>
            <Text style={styles.bannerText}>
              Status:{" "}
              {connected
                ? "🟢 Đã kết nối Realtime"
                : "🔴 Mất kết nối, đang thử lại..."}
            </Text>
          </View>

          <Text style={styles.listTitle}>
            Thành viên trực tuyến ({members.length}):
          </Text>
          <FlatList
            data={members}
            keyExtractor={(item) => item.userId}
            renderItem={renderMemberItem}
            contentContainerStyle={{ gap: 10 }}
            ListEmptyComponent={
              <Text style={styles.emptyText}>
                Chưa có thành viên nào khác trong phòng. Hãy rủ bạn bè vào nhé!
                👋
              </Text>
            }
          />
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#fff", padding: 20, paddingTop: 40 },
  title: { fontSize: 22, fontWeight: "bold", color: "#1E88E5" },
  subtitle: { fontSize: 13, color: "gray", marginBottom: 20 },
  joinBox: { flex: 1, justifyContent: "center", alignItems: "center", gap: 15 },
  infoText: { fontSize: 15, color: "#555", textAlign: "center" },
  statusBanner: {
    padding: 10,
    borderRadius: 8,
    backgroundColor: "#E8F5E9",
    marginBottom: 15,
  },
  bannerText: {
    fontSize: 13,
    fontWeight: "bold",
    color: "#2E7D32",
    textAlign: "center",
  },
  listTitle: {
    fontSize: 16,
    fontWeight: "bold",
    marginBottom: 10,
    color: "#333",
  },
  memberCard: {
    flexDirection: "row",
    alignItems: "center",
    padding: 12,
    borderRadius: 10,
    backgroundColor: "#F5F5F5",
    borderWidth: 1,
    borderColor: "#E0E0E0",
  },
  avatar: { fontSize: 28, marginRight: 12 },
  memberName: { fontSize: 15, fontWeight: "bold", color: "#333" },
  statusTag: {
    fontSize: 12,
    marginTop: 3,
    alignSelf: "flex-start",
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 4,
    overflow: "hidden",
  },
  focusTag: {
    backgroundColor: "#FFE0B2",
    color: "#E65100",
    fontWeight: "bold",
  },
  idleTag: { backgroundColor: "#E0E0E0", color: "#616161" },
  emptyText: {
    textAlign: "center",
    color: "gray",
    marginTop: 40,
    fontStyle: "italic",
  },
});
