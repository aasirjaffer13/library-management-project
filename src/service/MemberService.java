package service;

import model.Member;
import model.User;
import model.UserRole;
import repository.MemberRepository;
import repository.UserRepository;
import util.Validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    public MemberService() {
        this.memberRepository = new MemberRepository();
        this.userRepository = new UserRepository();
    }

    private String checkStaffAuth(int userId) {
        User user = userRepository.findById(userId);
        if (user == null) return "User not found";
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.LIBRARIAN && user.getRole() != UserRole.CLERK) {
            return "Permission denied: requires Administrator, Librarian, or Clerk role";
        }
        return null;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMember(int id) {
        return memberRepository.findById(id);
    }

    public Member getMemberByNumber(String memberNumber) {
        return memberRepository.findByMemberNumber(memberNumber);
    }

    public List<Member> searchMembers(String keyword) {
        return memberRepository.search(keyword);
    }

    public String addMember(String fullName, String email, String phone, String address, int userId) {
        String authErr = checkStaffAuth(userId);
        if (authErr != null) return authErr;

        if (Validation.isNullOrEmpty(fullName)) return "Full name is required";
        if (!Validation.isValidEmail(email)) return "Invalid email format";
        if (!Validation.isValidPhone(phone)) return "Invalid phone number (7-15 digits)";

        String memberNumber = generateMemberNumber();
        Member member = new Member(memberNumber, fullName, email, phone, address);
        int id = memberRepository.insert(member);
        return id > 0 ? null : "Failed to add member to database";
    }

    public String updateMember(Member member, int userId) {
        String authErr = checkStaffAuth(userId);
        if (authErr != null) return authErr;

        if (Validation.isNullOrEmpty(member.getFullName())) return "Full name is required";
        if (!Validation.isValidEmail(member.getEmail())) return "Invalid email format";
        if (!Validation.isValidPhone(member.getPhone())) return "Invalid phone number";

        boolean updated = memberRepository.update(member);
        return updated ? null : "Failed to update member";
    }

    public String deactivateMember(int memberId, int userId) {
        String authErr = checkStaffAuth(userId);
        if (authErr != null) return authErr;

        boolean deactivated = memberRepository.deactivate(memberId);
        return deactivated ? null : "Failed to deactivate member";
    }

    public int getActiveMemberCount() {
        return memberRepository.countActive();
    }

    private String generateMemberNumber() {
        int count = memberRepository.countAll();
        LocalDate now = LocalDate.now();
        return String.format("MEM-%s-%04d",
            now.format(DateTimeFormatter.ofPattern("yyyyMM")),
            count + 1
        );
    }
}
