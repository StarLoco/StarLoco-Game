local npc = Npc(525, 40)

npc.accessories = {0, 6863, 6886, 0, 7069}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2369, {1971, 1972})
    elseif answer == 1971 then p:ask(2370, {1975, 1976, 1977, 1978, 2025, 2026})
    elseif answer == 2026 then p:ask(2398, {2029, 2030, 2031, 2032, 2033, 2034, 2097})
    elseif answer == 1975 then p:compassTo(7444) p:endDialog()
    elseif answer == 1976 then p:compassTo(7394) p:endDialog()
    elseif answer == 1977 then p:compassTo(7443) p:endDialog()
    elseif answer == 1978 then p:compassTo(7397) p:endDialog()
    elseif answer == 2025 then p:compassTo(7289) p:endDialog()
    elseif answer == 2029 then p:compassTo(7412) p:endDialog()
    elseif answer == 2030 then p:compassTo(7399) p:endDialog()
    elseif answer == 2031 then p:compassTo(7350) p:endDialog()
    elseif answer == 2032 then p:compassTo(7348) p:endDialog()
    elseif answer == 2033 then p:compassTo(7344) p:endDialog()
    elseif answer == 2034 then p:ask(2370, {1975, 1976, 1977, 1978, 2025, 2026})
    elseif answer == 2097 then p:ask(2446, {2095, 2096, 2099, 2100, 2098})
    elseif answer == 2095 then p:compassTo(7384) p:endDialog()
    elseif answer == 2096 then p:compassTo(7349) p:endDialog()
    elseif answer == 2099 then p:compassTo(7346) p:endDialog()
    elseif answer == 2100 then p:compassTo(7345) p:endDialog()
    elseif answer == 2098 then p:ask(2398, {2029, 2030, 2031, 2032, 2033, 2034, 2097})
    elseif answer == 5730 then p:compassTo(7362) p:endDialog()
    end
end

RegisterNPCDef(npc)
